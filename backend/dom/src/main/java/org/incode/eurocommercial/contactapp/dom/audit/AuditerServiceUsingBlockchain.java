package org.incode.eurocommercial.contactapp.dom.audit;

import java.util.UUID;

import javax.inject.Inject;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.audit.AuditerService;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.iactn.Interaction;
import org.apache.isis.applib.services.publish.PublishedObjects;
import org.apache.isis.applib.services.publish.PublisherService;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class AuditerServiceUsingBlockchain implements AuditerService, PublisherService {

    AuditEntry auditEntry;

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Programmatic
    public void audit(
            final UUID transactionId,
            final int sequence,
            String targetClass,
            final Bookmark target,
            String memberIdentifier,
            final String propertyId,
            final String preValue,
            final String postValue,
            final String user,
            final java.sql.Timestamp timestamp
    ) {
        if (auditEntry == null) {
            auditEntry = createAuditEntry(timestamp, user, transactionId, sequence);
            auditEntry.addChange(target.toString(), propertyId, preValue, postValue);
        } else if (isCurrentlyBeingAudited(transactionId, sequence)) {
            auditEntry.addChange(target.toString(), propertyId, preValue, postValue);
        } else {
            commitAuditEntry();
            auditEntry = createAuditEntry(timestamp, user, transactionId, sequence);
            auditEntry.addChange(target.toString(), propertyId, preValue, postValue);
        }

//        System.out.println("Well Auditer works!");
//        try {
//            web3Service.getAuditTrailContract().audit(
//                    Strings.nullToEmpty(preValue),
//                    Strings.nullToEmpty(postValue)
//            ).send();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Programmatic
    public AuditEntry createAuditEntry(
            java.sql.Timestamp timestamp,
            String user,
            UUID transactionId,
            int sequence
    ) {
        AuditEntry auditEntry = new AuditEntry();
        auditEntry.setTimestamp(timestamp.getTime());
        auditEntry.setUser(user);
        auditEntry.setTransactionId(transactionId);
        auditEntry.setSequence(sequence);
        return auditEntry;
    }

    @Programmatic
    public boolean isCurrentlyBeingAudited(UUID transactionId, int sequence) {
        return auditEntry.getTransactionId().equals(transactionId) && auditEntry.getSequence() == sequence;
    }

    @Programmatic
    public void commitAuditEntry() {
        if (auditEntry == null) {
            return;
        }

        String auditData = auditEntry.serialise();
//        System.out.println(auditData);
        try {
            TransactionReceipt receipt = web3Service.getAuditTrailContract()
                    .audit(auditData)
                    .send();
            System.out.println(receipt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        auditEntry = null;
    }

    @Override public void publish(final Interaction.Execution<?, ?> execution) {
        commitAuditEntry();
    }

    @Override public void publish(final PublishedObjects publishedObjects) {
        commitAuditEntry();
    }

    @Inject private Web3Service web3Service;
}
