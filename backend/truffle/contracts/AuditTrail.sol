pragma solidity 0.4.23;

contract AuditTrail {
    address public owner;
    AuditEntry[] public auditEntries;

    struct AuditEntry {
        // private String user;
        // public String getUsername() {return getUser();}
        // private Timestamp timestamp;
        // private UUID transactionId;
        // private int sequence;
        // private String targetClass;
        // private String targetStr;
        // private String memberIdentifier;
        // private String propertyId;
        // string preValue;
        // string postValue;
        string auditData;
    }

    event Audit(string auditData);

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

    function audit(string auditData) external ownerOnly {
        auditEntries.push(AuditEntry(auditData));
        emit Audit(auditData);
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
