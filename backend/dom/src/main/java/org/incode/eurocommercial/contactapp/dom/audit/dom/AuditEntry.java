package org.incode.eurocommercial.contactapp.dom.audit.dom;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;

import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.LabelPosition;
import org.apache.isis.applib.annotation.MemberGroupLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.HasTransactionId;
import org.apache.isis.applib.services.HasUsername;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.util.TitleBuffer;
import org.apache.isis.objectstore.jdo.applib.service.DomainChangeJdoAbstract;
import org.apache.isis.objectstore.jdo.applib.service.JdoColumnLength;
import org.apache.isis.objectstore.jdo.applib.service.Util;

import org.incode.eurocommercial.contactapp.dom.audit.AuditModule;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "isisaudit",
        table="AuditEntry")
@DatastoreIdentity(
        strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
        column="id")
@Queries( {
    @Query(
            name="findRecentByTargetAndPropertyId", language="JDOQL",
            value="SELECT "
                    + "FROM org.incode.eurocommercial.contactapp.dom.audit.dom.AuditEntry "
                    + "WHERE targetStr == :targetStr "
                    + "&&    propertyId == :propertyId "
                    + "ORDER BY timestamp DESC "
                    + "RANGE 0,30"),
    @Query(
            name="findByTransactionId", language="JDOQL",
            value="SELECT "
                    + "FROM org.incode.eurocommercial.contactapp.dom.audit.dom.AuditEntry "
                    + "WHERE transactionId == :transactionId"),
    @Query(
            name="findByTargetAndTimestampBetween", language="JDOQL",  
            value="SELECT "
                    + "FROM org.incode.eurocommercial.contactapp.dom.audit.dom.AuditEntry "
                    + "WHERE targetStr == :targetStr " 
                    + "&&    timestamp >= :from "
                    + "&&    timestamp <= :to "
                    + "ORDER BY timestamp DESC"),
    @Query(
            name="findByTargetAndTimestampAfter", language="JDOQL",  
            value="SELECT "
                    + "FROM org.incode.eurocommercial.contactapp.dom.audit.dom.AuditEntry "
                    + "WHERE targetStr == :targetStr " 
                    + "&&    timestamp >= :from "
                    + "ORDER BY timestamp DESC"),
    @Query(
            name="findByTargetAndTimestampBefore", language="JDOQL",  
            value="SELECT "
                    + "FROM org.incode.eurocommercial.contactapp.dom.audit.dom.AuditEntry "
                    + "WHERE targetStr == :targetStr " 
                    + "&&    timestamp <= :to "
                    + "ORDER BY timestamp DESC"),
    @Query(
            name="findByTarget", language="JDOQL",  
            value="SELECT "
                    + "FROM org.incode.eurocommercial.contactapp.dom.audit.dom.AuditEntry "
                    + "WHERE targetStr == :targetStr " 
                    + "ORDER BY timestamp DESC"),
    @Query(
            name="findByTimestampBetween", language="JDOQL",  
            value="SELECT "
                    + "FROM org.incode.eurocommercial.contactapp.dom.audit.dom.AuditEntry "
                    + "WHERE timestamp >= :from " 
                    + "&&    timestamp <= :to "
                    + "ORDER BY timestamp DESC"),
    @Query(
            name="findByTimestampAfter", language="JDOQL",  
            value="SELECT "
                    + "FROM org.incode.eurocommercial.contactapp.dom.audit.dom.AuditEntry "
                    + "WHERE timestamp >= :from "
                    + "ORDER BY timestamp DESC"),
    @Query(
            name="findByTimestampBefore", language="JDOQL",  
            value="SELECT "
                    + "FROM org.incode.eurocommercial.contactapp.dom.audit.dom.AuditEntry "
                    + "WHERE timestamp <= :to "
                    + "ORDER BY timestamp DESC"),
    @Query(
            name="find", language="JDOQL",  
            value="SELECT "
                    + "FROM org.incode.eurocommercial.contactapp.dom.audit.dom.AuditEntry "
                    + "ORDER BY timestamp DESC")
})
@Indices({
    @Index(name="AuditEntry_ak", unique="true",
            columns={
                @Column(name="transactionId"),
                @Column(name="sequence"),
                @Column(name="target"),
                @Column(name="propertyId")
                })
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "isisaudit.AuditEntry"
)
@MemberGroupLayout(
        columnSpans={6,0,6,12},
        left={"Identifiers","Target"},
        right={"Detail","Metadata"})
public class AuditEntry extends DomainChangeJdoAbstract implements HasTransactionId, HasUsername {

    //region > domain events
    public static abstract class PropertyDomainEvent<T> extends AuditModule.PropertyDomainEvent<AuditEntry, T> {
    }
    //endregion

    public AuditEntry() {
        super(ChangeType.AUDIT_ENTRY);
    }

    //region > title

    public String title() {

        // nb: not thread-safe
        // formats defined in https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        final TitleBuffer buf = new TitleBuffer();
        buf.append(format.format(getTimestamp()));
        buf.append(" - ", getTargetStr());
        buf.append(", ", getPropertyId());
        return buf.toString();
    }

    //endregion

    //region > user (property)
    public static class UserDomainEvent extends PropertyDomainEvent<String> {
    }

    @Column(allowsNull="false", length=JdoColumnLength.USER_NAME)
    @Property(
            domainEvent = UserDomainEvent.class
    )
    @PropertyLayout(
            hidden = Where.PARENTED_TABLES
    )
    @MemberOrder(name="Identifiers",sequence = "10")
    @Getter @Setter
    private String user;

    @Programmatic
    public String getUsername() {
        return getUser();
    }
    //endregion

    //region > timestamp (property)

    public static class TimestampDomainEvent extends PropertyDomainEvent<Timestamp> {
    }

    @Column(allowsNull="false")
    @Property(
            domainEvent = TimestampDomainEvent.class
    )
    @PropertyLayout(
            hidden = Where.PARENTED_TABLES
    )
    @MemberOrder(name="Identifiers",sequence = "20")
    @Getter @Setter
    private Timestamp timestamp;

    //endregion

    //region > transactionId (property)

    public static class TransactionIdDomainEvent extends PropertyDomainEvent<UUID> {
    }

    /**
     * The unique identifier (a GUID) of the interaction in which this audit entry was persisted.
     *
     * <p>
     * The combination of (({@link #getTransactionId() transactionId}, {@link #getSequence() sequence}) makes up the
     * unique transaction identifier.
     * </p>
     *
     * <p>
     * The combination of ({@link #getTransactionId() transactionId}, {@link #getSequence()}, {@link #getTargetStr() target}, {@link #getPropertyId() propertyId} ) makes up the
     * alternative key.
     * </p>
     */
    @Column(allowsNull="false", length=JdoColumnLength.TRANSACTION_ID)
    @Property(
            domainEvent = TransactionIdDomainEvent.class,
            editing = Editing.DISABLED
    )
    @PropertyLayout(
            hidden=Where.PARENTED_TABLES,
            typicalLength = 36
    )
    @MemberOrder(name="Identifiers",sequence = "30")
    @Getter @Setter
    private UUID transactionId;

    //endregion

    //region > sequence (property)

    public static class SequenceDomainEvent extends PropertyDomainEvent<UUID> {
    }

    /**
     * The 0-based sequence number of the transaction in which this audit entry was persisted.
     *
     * <p>
     * The combination of (({@link #getTransactionId() transactionId}, {@link #getSequence() sequence}) makes up the
     * unique transaction identifier.
     * </p>
     *
     * <p>
     * The combination of (({@link #getTransactionId() transactionId}, {@link #getSequence() sequence}, {@link #getTargetStr() target}, {@link #getPropertyId() propertyId} ) makes up the
     * alternative key.
     * </p>
     */
    @Column(allowsNull="false")
    @Property(
            domainEvent = SequenceDomainEvent.class,
            editing = Editing.DISABLED
    )
    @PropertyLayout(
            hidden=Where.PARENTED_TABLES
    )
    @MemberOrder(name="Identifiers",sequence = "40")
    @Getter @Setter
    private int sequence;

    //endregion

    //region > targetClass (property)

    public static class TargetClassDomainEvent extends PropertyDomainEvent<String> {
    }

    @Column(allowsNull="true", length=JdoColumnLength.TARGET_CLASS)
    @Property(
            domainEvent = TargetClassDomainEvent.class
    )
    @PropertyLayout(
            named = "Class",
            typicalLength = 30
    )
    @MemberOrder(name="Target", sequence = "10")
    @Getter
    private String targetClass;

    public void setTargetClass(final String targetClass) {
        this.targetClass = Util.abbreviated(targetClass, JdoColumnLength.TARGET_CLASS);
    }

    //endregion

    //region > targetStr (property)

    public static class TargetStrDomainEvent extends PropertyDomainEvent<String> {
    }

    @Column(allowsNull="true", length=JdoColumnLength.BOOKMARK, name="target")
    @Property(
            domainEvent = TargetStrDomainEvent.class
    )
    @PropertyLayout(
            named = "Object"
    )
    @MemberOrder(name="Target", sequence="30")
    @Getter @Setter
    private String targetStr;
    //endregion

    //region > memberIdentifier (property)

    public static class MemberIdentifierDomainEvent extends PropertyDomainEvent<String> {
    }

    /**
     * This is the fully-qualified class and property Id, as per
     * {@link Identifier#toClassAndNameIdentityString()}.
     */
    @Column(allowsNull="true", length=JdoColumnLength.MEMBER_IDENTIFIER)
    @Property(
            domainEvent = MemberIdentifierDomainEvent.class
    )
    @PropertyLayout(
            typicalLength = 60,
            hidden = Where.ALL_TABLES
    )
    @MemberOrder(name="Detail",sequence = "1")
    @Getter
    private String memberIdentifier;

    public void setMemberIdentifier(final String memberIdentifier) {
        this.memberIdentifier = Util.abbreviated(memberIdentifier, JdoColumnLength.MEMBER_IDENTIFIER);
    }
    //endregion

    //region > propertyId (property)

    public static class PropertyIdDomainEvent extends PropertyDomainEvent<String> {
    }

    /**
     * This is the property name (without the class).
     */
    @Column(allowsNull="true", length=JdoColumnLength.AuditEntry.PROPERTY_ID)
    @Property(
            domainEvent = PropertyIdDomainEvent.class
    )
    @PropertyLayout(
            hidden = Where.NOWHERE
    )
    @MemberOrder(name="Target",sequence = "20")
    @Getter
    private String propertyId;

    public void setPropertyId(final String propertyId) {
        this.propertyId = Util.abbreviated(propertyId, JdoColumnLength.AuditEntry.PROPERTY_ID);
    }

    //endregion

    //region > preValue (property)

    public static class PreValueDomainEvent extends PropertyDomainEvent<String> {
    }

    @Column(allowsNull="true", length=JdoColumnLength.AuditEntry.PROPERTY_VALUE)
    @Property(
            domainEvent = PreValueDomainEvent.class
    )
    @PropertyLayout(
            hidden = Where.NOWHERE
    )
    @MemberOrder(name="Detail",sequence = "6")
    @Getter
    private String preValue;

    public void setPreValue(final String preValue) {
        this.preValue = Util.abbreviated(preValue, JdoColumnLength.AuditEntry.PROPERTY_VALUE);
    }
    //endregion

    //region > postValue (property)

    public static class PostValueDomainEvent extends PropertyDomainEvent<String> {
    }

    @Column(allowsNull="true", length=JdoColumnLength.AuditEntry.PROPERTY_VALUE)
    @Property(
            domainEvent = PostValueDomainEvent.class
    )
    @PropertyLayout(
            hidden = Where.NOWHERE
    )
    @MemberOrder(name="Detail",sequence = "7")
    @Getter
    private String postValue;

    public void setPostValue(final String postValue) {
        this.postValue = Util.abbreviated(postValue, JdoColumnLength.AuditEntry.PROPERTY_VALUE);
    }

    //endregion

    //region > metadata region dummy property

    public static class MetadataRegionDummyPropertyDomainEvent extends PropertyDomainEvent<String> { }

    /**
     * Exists just that the Wicket viewer will render an (almost) empty metadata region (on which the
     * framework contributed mixin actions will be attached).  The field itself can optionally be hidden
     * using CSS.
     */
    @NotPersistent
    @Property(domainEvent = MetadataRegionDummyPropertyDomainEvent.class, notPersisted = true)
    @PropertyLayout(labelPosition = LabelPosition.NONE, hidden = Where.ALL_TABLES)
    @MemberOrder(name="Metadata", sequence = "1")
    public String getMetadataRegionDummyProperty() {
        return null;
    }
    //endregion

    //region > helpers: toString

    @Override
    public String toString() {
        return ObjectContracts.toString(this, "timestamp,user,targetStr,memberIdentifier");
    }
    //endregion
}
