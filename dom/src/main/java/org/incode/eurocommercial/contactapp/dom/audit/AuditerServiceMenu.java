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
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    public List<AuditEntry> findByChangedObject(final String target) {
        return auditerServiceRepository.findByChangedObject(target);
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
