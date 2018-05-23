pragma solidity 0.4.23;

contract AuditTrail {
    address public owner;

    bytes28[] public auditedTransactions;
    function getAuditedTransactionsCount() public view returns(uint256) {
        return auditedTransactions.length;
    }

    mapping(bytes28 => bytes32) public transactionHashes;

    event Audit(bytes28 transactionIdentifier, bytes32 transactionHash);

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

    function audit(bytes28 transactionIdentifier, bytes32 transactionHash) external ownerOnly {
        require(transactionHashes[transactionIdentifier] == 0, "A transaction can only be audited once");
        transactionHashes[transactionIdentifier] = transactionHash;
        auditedTransactions.push(transactionIdentifier);
        emit Audit(transactionIdentifier, transactionHash);
    }

    function validate(bytes28 transactionIdentifier, bytes32 transactionHash) external view returns(uint8) {
        return transactionHashes[transactionIdentifier] == transactionHash ? 0 : 1;
    }
}
