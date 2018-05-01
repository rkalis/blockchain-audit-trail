package org.incode.eurocommercial.contactapp.dom.audit;

import java.util.UUID;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.audit.AuditerService;
import org.apache.isis.applib.services.bookmark.Bookmark;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class AuditerServiceUsingBlockchain implements AuditerService {

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

    }

    @Inject Web3Service web3Service;
}
