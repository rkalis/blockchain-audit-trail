pragma solidity 0.4.23;

contract AuditTrail {
    address public owner;

    bytes28[] public auditedTransactions;
    function getAuditedTransactionsCount() public view returns(uint256) {
        return auditedTransactions.length;
    }

    mapping(bytes28 => bytes32) public transactionHashes;

    event Audit(bytes28 transactionIdentifier, bytes32 transactionHash);
    event Validate(bytes28 transactionIdentifier, bytes32 transactionHash);

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
        if (transactionHashes[transactionIdentifier] == transactionHash) {
            return 0;
        } else {
            return 1;
        }
        // emit Validate(transactionIdentifier, transactionHash);
    }
}

// pragma solidity 0.4.23;

// contract AuditTrail {
//     address public owner;
//     AuditEntry[] public auditEntries;

//     struct AuditEntry {
//         bytes32 user;
//         uint256 timestamp;
//         bytes16 transactionId; // UUID
//         uint8 sequence;
//         string targetClass;
//         string targetStr;
//         string memberIdentifier;
//         string propertyId;
//         string preValue;
//         string postValue;
//     }

//     event Audit(
//         bytes32 user,
//         uint256 timestamp,
//         bytes16 transactionId,
//         uint8 sequence,
//         string targetClass,
//         string targetStr,
//         string memberIdentifier,
//         string propertyId,
//         string preValue,
//         string postValue
//     );

//     modifier ownerOnly {
//         require(msg.sender == owner);
//         _;
//     }

//     constructor() public {
//         owner = msg.sender;
//     }

//     function kill() public ownerOnly {
//         selfdestruct(owner);
//     }

//     function audit(
//         bytes32 user,
//         uint256 timestamp,
//         bytes16 transactionId,
//         uint8 sequence,
//         string targetClass,
//         string targetStr,
//         string memberIdentifier,
//         string propertyId,
//         string preValue,
//         string postValue
//     ) external ownerOnly {
//         auditEntries.push(
//             AuditEntry(user, timestamp, transactionId, sequence, targetClass, targetStr, memberIdentifier, propertyId, preValue, postValue));
//         emit Audit(user, timestamp, transactionId, sequence, targetClass, targetStr, memberIdentifier, propertyId, preValue, postValue);
//     }
// }
