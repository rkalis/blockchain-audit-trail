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
package org.incode.eurocommercial.contactapp.dom.group;

import java.util.Collections;
import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.eurocommercial.contactapp.dom.contacts.ContactRepository;
import org.incode.eurocommercial.contactapp.dom.country.Country;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = ContactGroup.class
)
public class ContactGroupRepository {

    @Programmatic
    public java.util.List<ContactGroup> listAll() {
        final List<ContactGroup> contactGroups = container.allInstances(ContactGroup.class);
        Collections.sort(contactGroups);
        return contactGroups;
    }

    @Programmatic
    public List<ContactGroup> findByCountry(final Country country) {
        return container.allMatches(
                new org.apache.isis.applib.query.QueryDefault<>(
                        ContactGroup.class,
                        "findByCountry",
                        "country", country));
    }

    @Programmatic
    public ContactGroup findByCountryAndName(
            final Country country,
            final String name
    ) {
        return container.uniqueMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        ContactGroup.class,
                        "findByCountryAndName",
                        "country", country,
                        "name", name));
    }

    @Programmatic
    public java.util.List<ContactGroup> findByName(
            String regex
    ) {
        return container.allMatches(
                new org.apache.isis.applib.query.QueryDefault<>(
                        ContactGroup.class,
                        "findByName",
                        "regex", regex));
    }

    @Programmatic
    public ContactGroup create(final Country country, final String name) {
        final ContactGroup contactGroup = container.newTransientInstance(ContactGroup.class);
        contactGroup.setCountry(country);
        contactGroup.setName(name);
        container.persistIfNotAlready(contactGroup);
        return contactGroup;
    }

    @Programmatic
    public ContactGroup findOrCreate(
            final Country country,
            final String name
    ) {
        ContactGroup contactGroup = findByCountryAndName(country, name);
        if (contactGroup == null) {
            contactGroup = create(country, name);
        }
        return contactGroup;
    }

    @Programmatic
    public void delete(final ContactGroup contactGroup) {
        contactGroup.getContactRoles().stream().forEach(cr -> {
            // this is sufficient because CG -> CR is a dependent relationship (CR can't exist outside of a CG)
            contactGroup.getContactRoles().remove(cr);
        });
        repositoryService.remove(contactGroup);
    }


    @javax.inject.Inject
    RepositoryService repositoryService;

    @javax.inject.Inject
    org.apache.isis.applib.DomainObjectContainer container;

    @javax.inject.Inject
    ContactRepository contactRepository;

}
