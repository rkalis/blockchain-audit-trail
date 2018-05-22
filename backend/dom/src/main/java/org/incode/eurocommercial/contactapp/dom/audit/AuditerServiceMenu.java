package org.incode.eurocommercial.contactapp.dom.audit;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY
)
@DomainServiceLayout(
        named = "Activity",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "40"
)
public class AuditerServiceMenu {

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    public List<AuditEntry> allAuditEntries() {
        return auditerServiceRepository.allAuditEntries();
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(
            bookmarking = BookmarkPolicy.AS_ROOT,
            named = "Validate Audit Trail"
    )
    public ValidationReport doValidateAuditTrail() throws Exception {
        return auditerServiceRepository.validateAuditTrail();
    }

    @Inject private AuditerServiceRepository auditerServiceRepository;
}

