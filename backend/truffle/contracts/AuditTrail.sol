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
        string preValue;
        string postValue;
    }

    event Audit(string preValue, string postValue);

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

    function audit(string preValue, string postValue) external ownerOnly {
        auditEntries.push(AuditEntry(preValue, postValue));
        emit Audit(preValue, postValue);
    }
}
