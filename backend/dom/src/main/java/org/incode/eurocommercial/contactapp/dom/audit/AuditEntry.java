package org.incode.eurocommercial.contactapp.dom.audit;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;

import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.LocalDateTime;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.HasTransactionId;
import org.apache.isis.applib.services.HasUsername;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.repository.RepositoryService;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType= IdentityType.DATASTORE
)
@DomainObject(
        editing = Editing.DISABLED
)
public class AuditEntry implements HasTransactionId, HasUsername, Comparable<AuditEntry> {

    @Programmatic
    public static AuditEntry deserialise(String representation) {
        return new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation().create()
                .fromJson(representation, AuditEntry.class);
    }

    @Programmatic
    public String serialise() {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation().create();
        JsonElement jsonElement = gson.toJsonTree(this);
        jsonElement.getAsJsonObject().addProperty("timestamp", timestamp.getTime());
        return gson.toJson(jsonElement);
    }

    @Programmatic
    public byte[] getHash() {
        return DigestUtils.sha256(serialise());
    }

    @Programmatic
    public byte[] getIdentifier() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(28);
        byteBuffer.putLong(transactionId.getMostSignificantBits());
        byteBuffer.putLong(transactionId.getLeastSignificantBits());
        byteBuffer.putInt(sequence);
        byteBuffer.putLong(timestamp.getTime());
        return byteBuffer.array();
    }

    @Expose
    @Column(allowsNull = "false")
    @Getter @Setter
    private String user;

    @Override
    public String getUsername() {
        return user;
    }

    @Column(allowsNull = "false")
    @Getter @Setter
    private Timestamp timestamp;

    @Expose
    @Column(allowsNull = "false")
    @Getter @Setter
    private UUID transactionId;

    @Expose
    @Column(allowsNull = "false")
    @Getter @Setter
    private int sequence;

    @Column(allowsNull = "true")
    @Getter @Setter
    private String ethTransactionHash;

    @Column(allowsNull = "true")
    @Getter @Setter
    private ValidationResult validationResult;

    @Persistent
    @Column(allowsNull = "true")
    @Getter @Setter
    private LocalDateTime lastValidatedAt;

    @Persistent(mappedBy = "auditEntry", dependentElement = "true")
    @CollectionLayout(defaultView = "table")
    @Getter @Setter
    private SortedSet<ChangedProperty> changedProperties = new TreeSet<>();

    @Action
    public AuditEntry validate() throws Exception {
        TransactionReceipt receipt = web3Service.getAuditTrailContract().validate(getIdentifier(), getHash()).send();
        validationResult = receipt.getStatus().equals("0x0") ? ValidationResult.INVALIDATED : ValidationResult.VALIDATED;
        lastValidatedAt = clockService.nowAsLocalDateTime();
        return this;
    }

    @Programmatic
    public void addChange(String target, String property, String preValue, String postValue) {
        ChangedProperty changedProperty = repositoryService.instantiate(ChangedProperty.class);
        changedProperty.setAuditEntry(this);
        changedProperty.setTarget(target);
        changedProperty.setProperty(property);
        changedProperty.setPreValue(preValue);
        changedProperty.setPostValue(postValue);
        repositoryService.persist(changedProperty);
        changedProperties.add(changedProperty);
    }

    @Override
    public int compareTo(final AuditEntry other) {
        return Comparator.comparing(AuditEntry::getUser)
                .thenComparing(AuditEntry::getTimestamp)
                .thenComparing(AuditEntry::getTransactionId)
                .thenComparing(AuditEntry::getSequence)
                .compare(this, other);
    }

    @PersistenceCapable(
            identityType= IdentityType.DATASTORE
    )
    @DomainObject(
            editing = Editing.DISABLED
    )
    public static class ChangedProperty implements Comparable<ChangedProperty> {

        @Column(allowsNull = "false")
        @PropertyLayout(hidden = Where.REFERENCES_PARENT)
        @Getter @Setter
        private AuditEntry auditEntry;

        @Expose
        @Column(allowsNull = "false")
        @Getter @Setter
        private String target;

        @Expose
        @Column(allowsNull = "false")
        @Getter @Setter
        private String property;

        @Expose
        @Column(allowsNull = "true")
        @Getter @Setter
        private String preValue;

        @Expose
        @Column(allowsNull = "true")
        @Getter @Setter
        private String postValue;

        @Override
        public int compareTo(final ChangedProperty other) {
            return Comparator.comparing(ChangedProperty::getAuditEntry)
                    .thenComparing(ChangedProperty::getTarget)
                    .thenComparing(ChangedProperty::getProperty)
                    .compare(this, other);
        }
    }

    public static enum ValidationResult {
        VALIDATED,
        INVALIDATED
    }

    @Inject RepositoryService repositoryService;
    @Inject Web3Service web3Service;
    @Inject ClockService clockService;
}
