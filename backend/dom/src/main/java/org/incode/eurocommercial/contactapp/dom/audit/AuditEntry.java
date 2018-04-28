package org.incode.eurocommercial.contactapp.dom.audit;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

import org.web3j.tuples.generated.Tuple2;

import org.apache.isis.applib.services.HasTransactionId;
import org.apache.isis.applib.services.HasUsername;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.util.TitleBuffer;
import org.apache.isis.objectstore.jdo.applib.service.DomainChangeJdoAbstract;

import lombok.Getter;
import lombok.Setter;

public class AuditEntry extends DomainChangeJdoAbstract implements HasTransactionId, HasUsername {
    public AuditEntry() {
        super(ChangeType.AUDIT_ENTRY);
    }

    public static AuditEntry fromTuple(Tuple2 tuple) {
        AuditEntry auditEntry = new AuditEntry();
        auditEntry.setPreValue(tuple.getValue1().toString());
        auditEntry.setPostValue(tuple.getValue2().toString());
        return auditEntry;
    }

    //region > title

    public String title() {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        final TitleBuffer buf = new TitleBuffer();
        buf.append(format.format(getTimestamp()));
        buf.append(" - ", getTargetStr());
        buf.append(", ", getPropertyId());
        return buf.toString();
    }

    //endregion

    //region > user (property)
    @Getter @Setter
    private String user;

    public String getUsername() {
        return getUser();
    }
    //endregion

    //region > timestamp (property)
    @Getter @Setter
    private Timestamp timestamp;
    //endregion

    //region > transactionId (property)
    @Getter @Setter
    private UUID transactionId;
    //endregion

    //region > sequence (property)
    @Getter @Setter
    private int sequence;
    //endregion

    //region > targetClass (property)
    @Getter @Setter
    private String targetClass;
    //endregion

    //region > targetStr (property)
    @Getter @Setter
    private String targetStr;
    //endregion

    //region > memberIdentifier (property)
    @Getter @Setter
    private String memberIdentifier;
    //endregion

    //region > propertyId (property)
    @Getter @Setter
    private String propertyId;
    //endregion

    //region > preValue (property)
    @Getter @Setter
    private String preValue;
    //endregion

    //region > postValue (property)
    @Getter @Setter
    private String postValue;
    //endregion

    @Override
    public String toString() {
        return ObjectContracts.toString(this, "timestamp,user,targetStr,memberIdentifier");
    }
}
