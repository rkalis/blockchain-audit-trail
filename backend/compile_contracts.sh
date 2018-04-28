#! /usr/bin/env sh

# Move to the script directory
DIR=$(dirname "$0")
cd "$DIR"

# Variables
JAVA_BASE_DIRECTORY="dom/src/main/java"
CONTRACTS_PACKAGE="org.incode.eurocommercial.contactapp.dom.audit.contracts.generated"
TRUFFLE_DIR="truffle"

declare -a CONTRACT_FILES=()

main() {
    pushd $TRUFFLE_DIR &>/dev/null
    compile
    deploy
    popd &>/dev/null
    generate
}
# Compile smart contracts and save the ones that have been compiled
compile() {
    OUTPUT=$(truffle compile)
    EXIT_STATUS=$?
    echo "${OUTPUT}"
    add_contract_files "${OUTPUT}"
    exit_if_failed $EXIT_STATUS
}

# Deploy if the --deploy flag is set
deploy() {
    if [[ $* == *--deploy* ]]; then
        truffle deploy
    fi
    exit_if_failed $?
}

# Generate web3j contract objects only from the contracts that were compiled
generate() {
    for contract in ${CONTRACT_FILES[@]}; do
        web3j truffle generate $contract -o $JAVA_BASE_DIRECTORY -p $CONTRACTS_PACKAGE
    done
}

# Add compiled contract json files to the list to be generated
add_contract_files() {
    while read line; do
        if [[ $line != Compiling* ]]; then
            continue
        fi
        CONTRACT=${line##"Compiling ./contracts/"}
        CONTRACT=${CONTRACT%%".sol..."}
        CONTRACT_FILES+=("$TRUFFLE_DIR/build/contracts/$CONTRACT.json")
    done <<< "$1"
}

exit_if_failed() {
    if [[ $1 -ne 0 ]]; then
        exit $1
    fi
}

main "$@"
