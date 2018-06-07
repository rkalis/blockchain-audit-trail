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

import java.sql.Timestamp;
import java.util.UUID;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.audit.AuditerService;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService2;
import org.apache.isis.applib.services.iactn.Interaction;
import org.apache.isis.applib.services.publish.PublishedObjects;
import org.apache.isis.applib.services.publish.PublisherService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.core.runtime.authentication.standard.SimpleSession;
import org.apache.isis.core.runtime.sessiontemplate.AbstractIsisSessionTemplate;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class AuditerServiceUsingBlockchain implements AuditerService, PublisherService {

    final ThreadLocal<AuditEntry> currentAuditEntry = new ThreadLocal<>();

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
            final Timestamp timestamp
    ) {
        AuditEntry auditEntry = currentAuditEntry.get();
        if (auditEntry == null) {
            auditEntry = createAuditEntry(timestamp, user, transactionId, sequence);
            auditEntry.addChange(target.toString(), propertyId, preValue, postValue);
            currentAuditEntry.set(auditEntry);
        } else if (isCurrentlyBeingAudited(transactionId, sequence)) {
            auditEntry.addChange(target.toString(), propertyId, preValue, postValue);
        } else {
            commitAuditEntry();
            auditEntry = createAuditEntry(timestamp, user, transactionId, sequence);
            auditEntry.addChange(target.toString(), propertyId, preValue, postValue);
            currentAuditEntry.set(auditEntry);
        }
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
        repositoryService.persistAndFlush(auditEntry);

        return auditEntry;
    }

    @Programmatic
    public boolean isCurrentlyBeingAudited(UUID transactionId, int sequence) {
        return currentAuditEntry.get().getTransactionId().equals(transactionId) && currentAuditEntry.get().getSequence() == sequence;
    }

    @Programmatic
    public void commitAuditEntry() {
        AuditEntry auditEntry = currentAuditEntry.get();
        if (auditEntry == null) {
            return;
        }

        try {
            final Bookmark bookmark = bookmarkService2.bookmarkFor(auditEntry);
            web3Service.getAuditTrailContract()
                    .audit(auditEntry.getIdentifier(), auditEntry.getHash())
                    .sendAsync()
                    .thenAccept(new TransactionReceiptConsumer(bookmark))
                    .exceptionally(e -> {e.printStackTrace(); return null;})
                    .thenRun(() -> System.out.println(currentAuditEntry.get().serialise()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentAuditEntry.set(null);
    }

    @Override public void publish(final Interaction.Execution<?, ?> execution) {
        commitAuditEntry();
    }

    @Override public void publish(final PublishedObjects publishedObjects) {
        commitAuditEntry();
    }

    @Inject private Web3Service web3Service;
    @Inject private RepositoryService repositoryService;
    @Inject private BookmarkService2 bookmarkService2;

    private static class TransactionReceiptConsumer extends AbstractIsisSessionTemplate implements Consumer<TransactionReceipt> {
        private Bookmark bookmark;

        public TransactionReceiptConsumer(final Bookmark bookmark) {
            this.bookmark = bookmark;
        }
        public TransactionReceiptConsumer() {}

        @Override
        public void accept(final TransactionReceipt receipt) {
            execute(new SimpleSession("sven", new String[]{}), receipt);
            System.out.println(receipt);
        }

        @Override protected void doExecuteWithTransaction(final Object context) {
            TransactionReceipt receipt = (TransactionReceipt) context;
            AuditEntry auditEntry = bookmarkService2.lookup(bookmark, AuditEntry.class);
            auditEntry.setEthTransactionHash(receipt.getTransactionHash());
        }

        @Inject private BookmarkService2 bookmarkService2;
    }
}
