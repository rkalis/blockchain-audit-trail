pragma solidity 0.4.23;

contract AuditTrail {
    address public owner;

    bytes28[] public auditedTransactions;
    function getAuditedTransactionsCount() public view returns(uint256) {
        return auditedTransactions.length;
    }

    mapping(bytes28 => bytes32) public dataHashes;

    event Audit(bytes28 transactionIdentifier, bytes32 dataHash);

    modifier ownerOnly {
        require(msg.sender == owner);
        _;
    }

    constructor() public {
        owner = msg.sender;
    }

    function kill() public ownerOnly {
        selfdestruct(owner);
    }

    function audit(bytes28 transactionIdentifier, bytes32 dataHash) external ownerOnly {
        require(dataHashes[transactionIdentifier] == 0, "A transaction can only be audited once");
        dataHashes[transactionIdentifier] = dataHash;
        auditedTransactions.push(transactionIdentifier);
        emit Audit(transactionIdentifier, dataHash);
    }

    function validate(bytes28 transactionIdentifier, bytes32 dataHash) external view returns(uint8) {
        return dataHashes[transactionIdentifier] == dataHash ? 0 : 1;
    }
}
