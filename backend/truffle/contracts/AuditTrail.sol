pragma solidity 0.4.23;

contract AuditTrail {
    address owner;
    AuditEntry[] auditEntries;

    struct AuditEntry {
        string value;
        // private String user;
        // public String getUsername() {return getUser();}
        // private Timestamp timestamp;
        // private UUID transactionId;
        // private int sequence;
        // private String targetClass;
        // private String targetStr;
        // private String memberIdentifier;
        // private String propertyId;
        // private String preValue;
        // private String postValue;
    }

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

    function audit(string value) external ownerOnly {
        auditEntries.push(AuditEntry(value));
    }
}
