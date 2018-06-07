/**
 * Copyright 2018 Rosco Kalis
 * <p>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.incode.eurocommercial.contactapp.dom.audit;

import java.math.BigInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import com.google.common.base.Strings;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.http.HttpService;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.config.ConfigurationService;

import org.incode.eurocommercial.contactapp.dom.audit.contracts.generated.AuditTrail;

import lombok.Getter;
import lombok.Setter;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class Web3Service {

    @Getter(onMethod = @__({@Programmatic}))
    private Web3j web3j;

    @Getter(onMethod = @__({@Programmatic}))
    @Setter(onMethod = @__({@Programmatic}))
    private Credentials credentials;

    @Getter(onMethod = @__({@Programmatic}))
    private AuditTrail auditTrailContract;

    @Getter(onMethod = @__({@Programmatic}))
    @Setter(onMethod = @__({@Programmatic}))
    private BigInteger gasPrice;

    @Getter(onMethod = @__({@Programmatic}))
    @Setter(onMethod = @__({@Programmatic}))
    private BigInteger gasLimit;

    @PostConstruct
    public void init() {
        String privateKey = configurationService.getProperty("ethereum.privateKey", GanacheAccounts.TEST_ACCOUNT_PRIVATE_KEYS.get(0));
        String networkUrl = configurationService.getProperty("ethereum.networkUrl", "http://localhost:8545");
        setProvider(new HttpService(networkUrl));
        setCredentials(Credentials.create(privateKey));

        String gasLimit = configurationService.getProperty("ethereum.gasLimit", "7000000");
        String gasPrice = configurationService.getProperty("ethereum.gasPrice", "2000000000");
        setGasLimit(new BigInteger(gasLimit));
        setGasPrice(new BigInteger(gasPrice));

        String auditTrailAddress = configurationService.getProperty("ethereum.auditTrailAddress");
        if (Strings.isNullOrEmpty(auditTrailAddress)) {
            deployAuditTrail();
        } else {
            loadDeployedAuditTrail(auditTrailAddress);
        }
    }

    @PreDestroy
    public void cleanup() {
        if (!Strings.isNullOrEmpty(configurationService.getProperty("killContract"))) {
            killAuditTrail();
        }
    }

    @Programmatic
    public void setProvider(Web3jService web3jService) {
        web3j = Web3j.build(web3jService);
    }

    @Programmatic
    public void deployAuditTrail() {
        try {
            auditTrailContract = AuditTrail.deploy(web3j, credentials, gasPrice, gasLimit).send();
        } catch (Exception e) {
            e.printStackTrace();
            auditTrailContract = null;
        }
    }

    @Programmatic
    public void killAuditTrail() {
        try {
            auditTrailContract.kill().send();
        } catch (Exception e) {
            e.printStackTrace();
            auditTrailContract = null;
        }
    }

    @Programmatic
    public void loadDeployedAuditTrail(String address) {
        auditTrailContract = AuditTrail.load(address, web3j, credentials, gasPrice, gasLimit);
    }

    @Inject private ConfigurationService configurationService;
}
