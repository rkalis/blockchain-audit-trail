package org.incode.eurocommercial.contactapp.dom.audit;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.commons.codec.binary.Hex;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.eurocommercial.contactapp.dom.audit.contracts.generated.AuditTrail;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class AuditerServiceRepository {

    @Programmatic
    public List<AuditEntry> allAuditEntries() {
        return repositoryService.allInstances(AuditEntry.class);
    }

    @Programmatic
    public AuditEntry findByTransactionIdAndSequence(
            UUID transactionId,
            int sequence
    ) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(AuditEntry.class,
                        "findByTransactionIdAndSequence",
                        "transactionId", transactionId,
                        "sequence", sequence));
    }

    @Programmatic
    public ValidationReport validateAuditTrail() throws Exception {
        AuditTrail auditTrail = web3Service.getAuditTrailContract();
        List<AuditEntry> validatedAuditEntries = Lists.newArrayList();
        List<AuditEntry> invalidatedAuditEntries = Lists.newArrayList();
        List<MissingAuditEntryViewModel> missingAuditEntries = Lists.newArrayList();

        for (AuditEntry entry : allAuditEntries()) {
            entry.validate();
            if (entry.getValidationResult() == AuditEntry.ValidationResult.VALIDATED) {
                validatedAuditEntries.add(entry);
            } else {
                invalidatedAuditEntries.add(entry);
            }
        }

        BigInteger auditedTransactionCount = auditTrail.getAuditedTransactionsCount().send();
        for (BigInteger i = BigInteger.ZERO;
             i.compareTo(auditedTransactionCount) < 0;
             i = i.add(BigInteger.ONE)) {
            byte[] identifier = auditTrail.auditedTransactions(i).send();

            ByteBuffer byteBuffer = ByteBuffer.wrap(identifier);
            UUID transactionId = new UUID(byteBuffer.getLong(), byteBuffer.getLong());
            int sequence = byteBuffer.getInt();
            Timestamp timestamp = new Timestamp(byteBuffer.getLong());

            AuditEntry foundEntry = findByTransactionIdAndSequence(transactionId, sequence);
            if (foundEntry == null) {
                String dataHash = Hex.encodeHexString(auditTrail.transactionHashes(identifier).send());
                MissingAuditEntryViewModel missingEntry = new MissingAuditEntryViewModel(timestamp, transactionId, sequence, dataHash);
                missingAuditEntries.add(missingEntry);
            }
        }

        return new ValidationReport(validatedAuditEntries, invalidatedAuditEntries, missingAuditEntries);
    }

    @Inject private RepositoryService repositoryService;
    @Inject private Web3Service web3Service;
}
