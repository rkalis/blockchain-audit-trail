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

import java.util.List;
import java.util.SortedSet;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.eurocommercial.contactapp.dom.contactable.ContactableEntity;
import org.incode.eurocommercial.contactapp.dom.group.ContactGroup;
import org.incode.eurocommercial.contactapp.dom.group.ContactGroupRepository;
import org.incode.eurocommercial.contactapp.dom.number.ContactNumber;
import org.incode.eurocommercial.contactapp.dom.number.ContactNumberSpec;
import org.incode.eurocommercial.contactapp.dom.role.ContactRole;
import org.incode.eurocommercial.contactapp.dom.role.ContactRoleRepository;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        named = "Contacts",
        menuOrder = "1"
)
public class ContactMenu {

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    @MemberOrder(sequence = "1")
    public java.util.List<Contact> listAll() {
        return contactRepository.listAll();
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    @MemberOrder(sequence = "2")
    public java.util.List<Contact> listOrphanedContacts() {
        return contactRepository.listOrphanedContacts();
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    @MemberOrder(sequence = "3")
    public java.util.List<Contact> find(
            final String query
    ) {
        String queryRegex = toCaseInsensitiveRegex(query);
        return contactRepository.find(queryRegex);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    @MemberOrder(sequence = "4")
    public java.util.List<Contact> findByGroup(
            final ContactGroup group
    ) {
        return contactRepository.findByContactGroup(group);
    }

    public List<ContactGroup> choices0FindByGroup() {
        return contactGroupRepository.listAll();
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    @MemberOrder(sequence = "5")
    public java.util.List<Contact> findByRole(
            @Parameter(maxLength = ContactRole.MaxLength.NAME)
            final String roleName
    ) {
        String roleNameRegex = toCaseInsensitiveRegex(roleName);
        return contactRepository.findByContactRoleName(roleNameRegex);
    }

    public SortedSet<String> choices0FindByRole() {
        return contactRoleRepository.roleNames();
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    @MemberOrder(sequence = "6")
    public java.util.List<Contact> findByEmail(
            final String query
    ) {
        String queryRegex = toCaseInsensitiveRegex(query);
        return contactRepository.findByEmail(queryRegex);
    }



    @Action
    @ActionLayout(cssClassFa = "fa fa-plus")
    @MemberOrder(sequence = "7")
    public Contact create(
            @Parameter(maxLength = ContactableEntity.MaxLength.NAME)
            final String name,
            @Parameter(maxLength = Contact.MaxLength.COMPANY, optionality = Optionality.OPTIONAL)
            final String company,
            @Parameter(maxLength = ContactNumber.MaxLength.NUMBER, optionality = Optionality.OPTIONAL, mustSatisfy = ContactNumberSpec.class)
            final String officeNumber,
            @Parameter(maxLength = ContactNumber.MaxLength.NUMBER, optionality = Optionality.OPTIONAL, mustSatisfy = ContactNumberSpec.class)
            final String mobileNumber,
            @Parameter(maxLength = ContactNumber.MaxLength.NUMBER, optionality = Optionality.OPTIONAL, mustSatisfy = ContactNumberSpec.class)
            final String homeNumber,
            @Parameter(maxLength = ContactableEntity.MaxLength.EMAIL, optionality = Optionality.OPTIONAL)
            final String email) {
        return contactRepository.create(name, company, email, null, officeNumber, mobileNumber, homeNumber);
    }

    public String validateCreate(
            final String name,
            final String company,
            final String officeNumber,
            final String mobileNumber,
            final String homeNumber,
            final String email) {
        if (!contactGroupRepository.findByName(name).isEmpty()) {
            return "This name is already in use by a contact group";
        } else {
            return contactRepository.findByName(name).isEmpty() ? null : "This name is already in use by another contact";
        }
    }

    public static String toCaseInsensitiveRegex(final String pattern) {
        if (pattern == null) {
            return null;
        }
        if (pattern.contains("*") || pattern.contains("?")) {
            final String regex = pattern.replace("*", ".*").replace("?", ".");
            return "(?i)" + regex;
        } else {
            return "(?i).*" + pattern + ".*";
        }
    }

    @javax.inject.Inject
    ContactRepository contactRepository;
    @javax.inject.Inject
    ContactGroupRepository contactGroupRepository;
    @javax.inject.Inject
    ContactRoleRepository contactRoleRepository;
}
