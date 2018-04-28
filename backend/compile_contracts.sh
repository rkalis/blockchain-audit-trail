#! /usr/bin/env sh

# Move to the script directory
DIR=$(dirname "$0")
cd "$DIR"

# Variables
JAVA_BASE_DIRECTORY="dom/src/main/java"
CONTRACTS_PACKAGE="org.incode.eurocommercial.contactapp.dom.audit.contracts.generated"
TRUFFLE_DIR="truffle"

# Compile and deploy (if --deploy flag is set)
pushd $TRUFFLE_DIR &>/dev/null
truffle compile
if [[ $* == *--deploy* ]]; then
    truffle deploy
fi
popd &>/dev/null

# Generate web3j contract objects
for contract in $TRUFFLE_DIR/build/contracts/*.json; do
    web3j truffle generate $contract -o $JAVA_BASE_DIRECTORY -p $CONTRACTS_PACKAGE
done
