/*
 *  Copyright 2015-2016 Eurocommercial Properties NV
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.incode.eurocommercial.contactapp.app.rest.v1.contacts;

import java.util.List;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.eurocommercial.contactapp.dom.contacts.ContactRepository;
import org.incode.eurocommercial.contactapp.dom.group.ContactGroupRepository;

@DomainService(
        nature = NatureOfService.VIEW_REST_ONLY
)
public class ContactableViewModelRepository {

    @Action(
            semantics = SemanticsOf.SAFE,
            typeOf = ContactableViewModel.class
    )
    public java.util.List<ContactableViewModel> listAll() {
        final List<ContactableViewModel> contactable = Lists.newArrayList();
        contactable.addAll(
                FluentIterable
                        .from(contactGroupRepository.listAll())
                        .transform(ContactableViewModel.createForGroup(container))
                        .toList());
        contactable.addAll(
                FluentIterable
                        .from(contactRepository.listAll())
                        .transform(ContactableViewModel.createForContact(container))
                        .toList());
        return contactable;
    }

    @javax.inject.Inject
    ContactGroupRepository contactGroupRepository;

    @javax.inject.Inject
    ContactRepository contactRepository;

    @javax.inject.Inject
    DomainObjectContainer container;
}
