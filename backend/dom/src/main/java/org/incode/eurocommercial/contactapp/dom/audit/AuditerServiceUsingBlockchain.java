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
import org.apache.isis.applib.services.repository.RepositoryService;

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
//            web3Service.getTransactionManager().setNonce(web3Service.getWeb3j().ethGetTransactionCount(web3Service.getCredentials().getAddress(), DefaultBlockParameterName.PENDING).send().getTransactionCount());
//            web3Service.getAuditTrailContract().audit(
////                    Strings.nullToEmpty(preValue),
//                    Strings.nullToEmpty(postValue)
//            ).sendAsync()
//                    .thenAccept(System.out::println)
//                    .exceptionally(e -> { e.printStackTrace(); return null;});
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
        AuditEntry auditEntry = repositoryService.instantiate(AuditEntry.class);
        auditEntry.setTimestamp(timestamp);
        auditEntry.setUser(user);
        auditEntry.setTransactionId(transactionId);
        auditEntry.setSequence(sequence);
        repositoryService.persist(auditEntry);

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

        try {
//            CompletableFuture.supplyAsync(auditEntry)
//                    .thenAcceptBoth(
//                            web3Service.getAuditTrailContract()
//                                    .audit(currentAuditEntry.getIdentifier(), currentAuditEntry.getHash())
//                                    .sendAsync(),
//                            (entry, transactionReceipt) -> entry.setEthTransactionHash(transactionReceipt.getTransactionHash())
//                    );
//
////                    .thenCombine(CompletableFuture.supplyAsync(() -> currentAuditEntry), (receipt, entry) -> {entry.setEthTransactionHash(receipt.getTransactionHash()); return null;});
////                    .thenAccept(receipt -> currentAuditEntry.setEthTransactionHash(receipt.getTransactionHash()))
////                    .exceptionally(e -> {currentAuditEntry.setEthTransactionHash("FAILED"); return null;})
////                    .thenRun(() -> System.out.println(currentAuditEntry.serialise()));
            TransactionReceipt receipt = web3Service.getAuditTrailContract()
                    .audit(auditEntry.getIdentifier(), auditEntry.getHash())
                    .send();
            auditEntry.setEthTransactionHash(receipt.getTransactionHash());
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
    @Inject private RepositoryService repositoryService;
}
