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
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.HasTransactionId;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService2;
import org.apache.isis.applib.services.metamodel.MetaModelService2;
import org.apache.isis.applib.services.metamodel.MetaModelService3;

@Mixin(method = "act")
public class Object_auditEntries {
    private final Object domainObject;

    public Object_auditEntries(final Object domainObject) {
        this.domainObject = domainObject;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(named="Audit Entries", defaultView = "table")
    public List<AuditEntry> act() {
        final Bookmark target = bookmarkService.bookmarkFor(domainObject);
        return auditerServiceRepository.findByChangedObject(target.toString());
    }

    public boolean hideAct() {
        MetaModelService2.Sort sort = metaModelService3.sortOf(domainObject.getClass(), MetaModelService3.Mode.RELAXED);
        return !(sort.isJdoEntity() || domainObject instanceof HasTransactionId) || act().size() == 0;
    }

    @Inject private AuditerServiceRepository auditerServiceRepository;
    @Inject private BookmarkService2 bookmarkService;
    @Inject private MetaModelService3 metaModelService3;
}
