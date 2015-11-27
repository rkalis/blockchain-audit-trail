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
package org.incode.eurocommercial.contactapp.dom.contacts;

import java.util.Collections;
import java.util.List;
import java.util.SortedSet;

import javax.inject.Inject;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;

import org.incode.eurocommercial.contactapp.dom.group.ContactGroup;
import org.incode.eurocommercial.contactapp.dom.group.ContactGroupRepository;
import org.incode.eurocommercial.contactapp.dom.number.ContactNumberType;
import org.incode.eurocommercial.contactapp.dom.role.ContactRole;
import org.incode.eurocommercial.contactapp.dom.role.ContactRoleRepository;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = Contact.class
)
public class ContactRepository {

    @Programmatic
    public java.util.List<Contact> listAll() {
        return asSortedList(container.allInstances(Contact.class));
    }

    @Programmatic
    public java.util.List<Contact> find(
            final String regex
    ) {
        java.util.SortedSet<Contact> contacts = Sets.newTreeSet();
        contacts.addAll(findByName(regex));
        contacts.addAll(findByCompany(regex));
        contacts.addAll(findByContactRoleName(regex));
        contacts.addAll(findByEmail(regex));
        for (ContactGroup contactGroup : contactGroupRepository.findByName(regex)) {
            contacts.addAll(findByContactGroup(contactGroup));
        }
        return asSortedList(contacts);
    }

    @Programmatic
    public java.util.List<Contact> findByName(
            final String regex
    ) {
        final List<Contact> contacts = container.allMatches(
                new QueryDefault<>(
                        Contact.class,
                        "findByName",
                        "regex", regex));
        return asSortedList(contacts);
    }

    @Programmatic
    public java.util.List<Contact> findByCompany(
            final String regex
    ) {
        final List<Contact> contacts = container.allMatches(
                new QueryDefault<>(
                        Contact.class,
                        "findByCompany",
                        "regex", regex));
        return asSortedList(contacts);
    }

    @Programmatic
    public java.util.List<Contact> findByContactGroup(
            ContactGroup contactGroup
    ) {
        java.util.SortedSet<Contact> contacts = Sets.newTreeSet();
        for (ContactRole contactRole : contactRoleRepository.findByGroup(contactGroup)) {
            contacts.add(contactRole.getContact());
        }
        return asSortedList(contacts);
    }

    @Programmatic
    public java.util.List<Contact> findByContactRoleName(
            String regex
    ) {
        java.util.SortedSet<Contact> contacts = Sets.newTreeSet();

        for (ContactRole contactRole : contactRoleRepository.findByName(regex)) {
            contacts.add(contactRole.getContact());
        }

        return asSortedList(contacts);
    }

    @Programmatic
    public java.util.List<Contact> findByEmail(
            String regex
    ) {
        final List<Contact> contacts = container.allMatches(
                new QueryDefault<>(
                        Contact.class,
                        "findByEmail",
                        "regex", regex));
        return asSortedList(contacts);
    }

    @Programmatic
    public java.util.List<Contact> listOrphanedContacts() {
        final List<Contact> contacts = container.allMatches(
                new QueryDefault<>(
                        Contact.class,
                        "listOrphanedContacts"));
        return asSortedList(contacts);
    }

    @Programmatic
    public Contact create(
            final String name,
            final String company,
            final String email,
            final String notes,
            final String officeNumber,
            final String mobileNumber,
            final String homeNumber) {
        final Contact contact = container.newTransientInstance(Contact.class);
        contact.setName(name);
        contact.setCompany(company);
        contact.setEmail(email);
        contact.setNotes(notes);
        container.persistIfNotAlready(contact);

        if (officeNumber != null) {
            contact.addContactNumber(officeNumber, ContactNumberType.OFFICE.title(), null);
        }
        if (mobileNumber != null) {
            contact.addContactNumber(mobileNumber, ContactNumberType.MOBILE.title(), null);
        }
        if (homeNumber != null) {
            contact.addContactNumber(homeNumber, ContactNumberType.HOME.title(), null);
        }

        return contact;
    }

    @Programmatic
    public Contact findOrCreate(
            final String name,
            final String company,
            final String email,
            final String notes,
            final String officeNumber,
            final String mobileNumber,
            final String homeNumber) {
        java.util.List<Contact> contacts = findByName(name);
        Contact contact;
        if (contacts.size() == 0) {
            contact = create(name, company, email, notes, officeNumber, mobileNumber, homeNumber);
        } else {
            contact = contacts.get(0);
        }
        return contact;
    }

    @Programmatic
    public void delete(final Contact contact) {
        final SortedSet<ContactRole> contactRoles = contact.getContactRoles();
        for (ContactRole contactRole : contactRoles) {
            container.removeIfNotAlready(contactRole);
        }
        container.removeIfNotAlready(contact);
    }

    private static List<Contact> asSortedList(final List<Contact> contacts) {
        Collections.sort(contacts);
        return contacts;
    }

    private static List<Contact> asSortedList(final SortedSet<Contact> contactsSet) {
        final List<Contact> contacts = Lists.newArrayList();
        // no need to sort, just copy over
        contacts.addAll(contactsSet);
        return contacts;
    }

    @javax.inject.Inject
    org.apache.isis.applib.DomainObjectContainer container;

    @Inject
    private ContactRoleRepository contactRoleRepository;

    @Inject
    private ContactGroupRepository contactGroupRepository;

}
