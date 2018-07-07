# Blockchain Audit Trail

This repository contains a fork of the [Incode Contact App](https://github.com/incodehq/contactapp), with a proof-of-concept blockchain audit trail implementation built on top of it. This blockchain audit trail is part of my Bachelor's thesis on validating audit trail data at the University of Amsterdam. The full version of this thesis is available [here](https://esc.fnwi.uva.nl/thesis/centraal/files/f1051832702.pdf), and a demo video of this implementation is available on [YouTube](https://youtu.be/nfekoK6pUqU)



## Contents

### Audit Trail
The audit trail implementation can be found [here](dom/src/main/java/org/incode/eurocommercial/contactapp/dom/audit), with the most important functionality residing in the [AuditerServiceUsingBlockchain class](dom/src/main/java/org/incode/eurocommercial/contactapp/dom/audit/AuditerServiceUsingBlockchain.java). It works by aggregating all changes inside a single database transaction, and saving this as an Audit Entry object. A hash of this Audit Entry object is then submitted to our smart contract using Web3j.

### Smart Contract
The Ethereum smart contract implementation can be found [here](truffle/contracts/AuditTrail.sol). This contract has the ability to audit Audit Entries by storing their identifier and data hash in a mapping. Next, it can validate these Audit Entries by comparing the stored identifier-hash pair against the one that is passed to the function. Finally, because it saves a list of all audited transaction identifiers, it is possible to check for missing Audit Entries in the application audit trail.

### Web3Service
The integration between this audit trail and the smart contract is made with Web3j, a Java library used for connecting with Ethereum based blockchains. To easily use Web3j in Apache Isis applications I created a Web3Service, which contains a web3j instance, the credentials to an Ethereum account, and a deployed instance of the AuditTrail smart contract.

The Web3Service gets its properties from Apache Isis configuration files such as [isis.properties](webapp/src/main/webapp/WEB-INF/isis.properties). This file already includes some of the configuration properties, specifically the following ones:
```
application.ethereum.auditTrailAddress
application.ethereum.gasLimit
application.ethereum.gasPrice
```
These properties grant the options to add a custom gas limit and gas price for the Ethereum transactions, and it allows the application to connect to an AuditTrail smart contract that has already been deployed. When omitted, a new contract will be deployed.

There are two configuration properties that still need to be configured in order to connect with a blockchain:
```
application.ethereum.privateKey
application.ethereum.networkUrl
```
The privateKey property specifies the private key of an Ethereum account, which will be used to send transactions to the AuditTrail smart contract. The networkUrl property specifies the URL used to connect with an Ethereum blockchain. If these are not specified, the application falls back on a locally running [Ganache](http://truffleframework.com/ganache/) instance with its test accounts, so be sure to have it running.

To generate the Java wrapper of the smart contract I created the [compile_contracts.sh](compile_contracts.sh) script, which uses the web3j and truffle CLIs, which should be installed in order to run the script.
```
brew tap web3j/web3j
brew install web3j
brew install truffle
```

## Locally running the demo application
### Prerequisites
To run this application with Jetty, Java and Maven need to be installed:
```
brew cask install java
brew install maven
```

To run this application with Docker, Docker needs to be installed:
```
brew cask install docker
```

Next, be sure to add your custom configuration properties to the [isis.properties](webapp/src/main/webapp/WEB-INF/isis.properties) file:
```
application.ethereum.privateKey
application.ethereum.networkUrl
```

Alternatively, spin up a local Ganache instance.

One of these two steps need to be executed, **otherwise the audit trail implementation will not function**.

### Running the application
#### Using Jetty
First, clone this repository and navigate into the repository:
```
git clone git@github.com:rkalis/blockchain-audit-trail.git
cd blockchain-audit-trail
```

Next, build the application with Maven:
```
mvn clean install -Djetty-run -DskipTests
```

Finally, navigate to the webapp folder and run the application with Jetty:
```
cd webapp
mvn jetty:run -Dorg.eclipse.jetty.annotations.maxWait=120
```

After some time you should see `[INFO] Started Jetty Server` in your console and you should be able to access the application at `localhost:8080/admin` with credentials admin/pass.

#### Using Docker
There are two Dockerfiles for this repository, the first Dockerfile copies the full repository to the Docker image, and builds it inside the image. The second one (Dockerfile_prebuilt) assumes that the project has already been built with Maven, and will only copy the built files to the image. Both files use Tomcat 8 with Oracle Java 8 to run the application.

The application can be run with Docker with the `dockerise.sh` or `dockerise_prebuilt.sh` scripts.
```
./dockerise.sh
./dockerise_prebuilt.sh
```

**Note that the Ganache+Docker combination has not been tested.**

## Application guide
The application is a simple contact management app. Contacts can be created and added to Groups. Every application interaction gets logged to the blockchain audit trail. In the Activity menu to the top right it is possible to see the entire audit trail, validate the audit trail against the blockchain, and search through it.
