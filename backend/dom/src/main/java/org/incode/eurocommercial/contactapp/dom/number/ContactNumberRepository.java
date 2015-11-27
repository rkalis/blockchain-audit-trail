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
package org.incode.eurocommercial.contactapp.dom.number;

import java.util.List;
import java.util.Set;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.eurocommercial.contactapp.dom.contactable.ContactableEntity;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = ContactNumber.class
)
public class ContactNumberRepository {

    @Programmatic
    public java.util.List<ContactNumber> listAll() {
        return container.allInstances(ContactNumber.class);
    }

    private ContactNumber findByOwnerAndNumber(
            final ContactableEntity owner,
            final String number) {
        return container.uniqueMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        ContactNumber.class,
                        "findByOwnerAndNumber",
                        "owner", owner,
                        "number", number));
    }

    private ContactNumber create(
            final ContactableEntity owner,
            final String number,
            final String type) {
        final ContactNumber contactNumber = container.newTransientInstance(ContactNumber.class);
        contactNumber.setOwner(owner);
        contactNumber.setType(type);
        contactNumber.setNumber(number);
        // required if the owner is not yet persistent (we turn persistence-by-reachability off).
        owner.getContactNumbers().add(contactNumber);
        container.persistIfNotAlready(contactNumber);
        return contactNumber;
    }

    @Programmatic
    public ContactNumber findByNumber(
            final String number) {
        return container.uniqueMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        ContactNumber.class,
                        "findByNumber",
                        "number", number));
    }

    @Programmatic
    public ContactNumber findOrCreate(
            final ContactableEntity owner,
            final String number,
            final String type) {
        ContactNumber contactNumber = findByOwnerAndNumber(owner, number);
        if (contactNumber == null) {
            contactNumber = create(owner, number, type);
        } else {
            contactNumber.setType(type);
        }
        return contactNumber;
    }

    @Programmatic
    public Set<String> existingTypes() {
        final Set<String> types = Sets.newTreeSet();
        types.addAll(ContactNumberType.titles());
        types.addAll(
                FluentIterable
                        .from(listAll())
                        .transform(ContactNumber::getType)
                        .toSet());
        return types;
    }

    @javax.inject.Inject
    org.apache.isis.applib.DomainObjectContainer container;
}
