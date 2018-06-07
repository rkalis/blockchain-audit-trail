/**
 * Copyright 2018 Rosco Kalis
 * <p>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.incode.eurocommercial.contactapp.dom.audit;

import java.math.BigInteger;
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
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;

import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.services.HasTransactionId;
import org.apache.isis.applib.services.HasUsername;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.schema.utils.jaxbadapters.JodaLocalDateTimeStringAdapter;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE
)
@Queries({
        @Query(
                name = "findByTransactionIdAndSequence", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.eurocommercial.contactapp.dom.audit.AuditEntry "
                        + "WHERE transactionId == :transactionId "
                        + "&& sequence == :sequence"),
        @Query(
                name = "findByChangedObject", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.eurocommercial.contactapp.dom.audit.AuditEntry "
                        + "WHERE this.changedProperties.contains(prop) && prop.target == :target "
                        + "VARIABLES org.incode.eurocommercial.contactapp.dom.audit.ChangedProperty prop"
        )
})
@DomainObject(
        editing = Editing.DISABLED,
        publishing = Publishing.DISABLED
)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class AuditEntry implements HasTransactionId, HasUsername, Comparable<AuditEntry> {

    public String title() {
        return user + "> " + transactionId + " [" + sequence + "]";
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

    @XmlTransient
    public String getDataHash() {
        return org.bouncycastle.util.encoders.Hex.toHexString(getHash());
    }

    @Expose
    @Column(allowsNull = "false")
    @Getter @Setter
    private String user;

    @Override
    @Programmatic
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
    @XmlJavaTypeAdapter(JodaLocalDateTimeStringAdapter.ForJaxb.class)
    @Column(allowsNull = "true")
    @Getter @Setter
    private LocalDateTime lastValidatedAt;

    @Persistent(mappedBy = "auditEntry", dependentElement = "true")
    @CollectionLayout(defaultView = "table")
    @Getter @Setter
    private SortedSet<ChangedProperty> changedProperties = new TreeSet<>();

    @Action
    public AuditEntry validate() {
        try {
            BigInteger validationStatus = web3Service.getAuditTrailContract().validate(getIdentifier(), getHash()).send();
            validationResult = validationStatus.equals(BigInteger.ZERO) ? ValidationResult.VALIDATED : ValidationResult.INVALIDATED;
            lastValidatedAt = clockService.nowAsLocalDateTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    public enum ValidationResult {
        VALIDATED,
        INVALIDATED
    }

    @Inject private RepositoryService repositoryService;
    @Inject private Web3Service web3Service;
    @Inject private ClockService clockService;
}
