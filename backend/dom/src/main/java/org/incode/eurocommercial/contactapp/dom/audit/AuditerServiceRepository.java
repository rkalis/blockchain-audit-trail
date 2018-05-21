package org.incode.eurocommercial.contactapp.dom.audit;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class AuditerServiceRepository {

    @Programmatic
    public List<AuditEntry> allAuditEntries() {
        return repositoryService.allInstances(AuditEntry.class);
    }

    @Inject private RepositoryService repositoryService;
}

