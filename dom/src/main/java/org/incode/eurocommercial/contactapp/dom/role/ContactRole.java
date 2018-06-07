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
package org.incode.eurocommercial.contactapp.dom.role;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.FluentIterable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberGroupLayout;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.incode.eurocommercial.contactapp.dom.contacts.Contact;
import org.incode.eurocommercial.contactapp.dom.contacts.ContactRepository;
import org.incode.eurocommercial.contactapp.dom.group.ContactGroup;
import org.incode.eurocommercial.contactapp.dom.group.ContactGroupRepository;
import org.incode.eurocommercial.contactapp.dom.util.StringUtil;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.DATE_TIME,
        column = "version")
@Queries({
        @Query(
                name = "findByName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.eurocommercial.contactapp.dom.role.ContactRole "
                        + "WHERE roleName.matches(:regex) "),
        @Query(
                name = "findByContactAndContactGroup", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.eurocommercial.contactapp.dom.role.ContactRole "
                        + "WHERE contact == :contact && contactGroup == :contactGroup "),
        @Query(
                name = "findByContact", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.eurocommercial.contactapp.dom.role.ContactRole "
                        + "WHERE contact == :contact "),
        @Query(
                name = "findByContactGroup", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.eurocommercial.contactapp.dom.role.ContactRole "
                        + "WHERE contactGroup == :contactGroup ")
})
@Unique(name = "ContactRole_roleName_UNQ", members = { "contact", "contactGroup" })
@DomainObject(
        editing = Editing.DISABLED,
        bounded = true
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
@MemberGroupLayout(
        columnSpans={6,0,0,6}
)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class ContactRole implements Comparable<ContactRole> {

    //region > title
    public static class MaxLength {
        private MaxLength(){}
        public static final int NAME = 50;
    }

    public String title() {
        final StringBuilder buf = new StringBuilder();
        buf.append(getContact().getName()).append(": ");
        if(getRoleName() != null) {
            buf.append(getRoleName());
        }
        buf.append(" in ").append(getContactGroup().getName());
        return buf.toString();
    }
    //endregion

    @Column(allowsNull = "false")
    @Property
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    @Getter @Setter
    private Contact contact;

    @Column(allowsNull = "false")
    @Property
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    @Getter @Setter
    private ContactGroup contactGroup;

    @Column(allowsNull = "true", length = MaxLength.NAME)
    @Property()
    @Getter @Setter
    private String roleName;

    //region > alsoInGroup (action)

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(position = ActionLayout.Position.PANEL, cssClassFa = "fa fa-plus")
    public ContactRole alsoInGroup(
            @Parameter(optionality = Optionality.MANDATORY)
            final ContactGroup contactGroup,
            @Parameter(maxLength = ContactRole.MaxLength.NAME, optionality = Optionality.OPTIONAL)
            final String role,
            @Parameter(maxLength = ContactRole.MaxLength.NAME, optionality = Optionality.OPTIONAL)
            final String newRole) {
        final String roleName = StringUtil.firstNonEmpty(newRole, role);
        final ContactRole newlyCreatedRole = contactRoleRepository.findOrCreate(contact, contactGroup, roleName);
        return newlyCreatedRole;
    }

    public List<ContactGroup> choices0AlsoInGroup() {
        final List<ContactGroup> contactGroups = contactGroupRepository.listAll();
        final List<ContactGroup> currentContactGroups =
                FluentIterable
                        .from(contact.getContactRoles())
                        .transform(ContactRole::getContactGroup)
                        .toList();
        contactGroups.removeAll(currentContactGroups);
        return contactGroups;
    }
    public SortedSet<String> choices1AlsoInGroup() {
        return contactRoleRepository.roleNames();
    }

    public String validateAlsoInGroup(final ContactGroup contactGroup, final String role, final String newRole) {
        return StringUtil.eitherOr(role, newRole, "role");
    }
    //endregion

    //region > alsoWithContact (action)


    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(position = ActionLayout.Position.PANEL, cssClassFa = "fa fa-plus")
    public ContactRole alsoWithContact(
            @Parameter(optionality = Optionality.MANDATORY)
            final Contact contact,
            @Parameter(maxLength = ContactRole.MaxLength.NAME, optionality = Optionality.OPTIONAL)
            final String role,
            @Parameter(maxLength = ContactRole.MaxLength.NAME, optionality = Optionality.OPTIONAL)
            final String newRole) {
        final String roleName = StringUtil.firstNonEmpty(newRole, role);
        final ContactRole newlyCreatedRole = contactRoleRepository.findOrCreate(contact, contactGroup, roleName);
        return newlyCreatedRole;
    }

    public List<Contact> choices0AlsoWithContact() {
        final List<Contact> contacts = contactRepository.listAll();
        final List<Contact> currentContacts =
                FluentIterable
                        .from(contactGroup.getContactRoles())
                        .transform(ContactRole::getContact)
                        .toList();
        contacts.removeAll(currentContacts);
        return contacts;
    }
    public SortedSet<String> choices1AlsoWithContact() {
        return contactRoleRepository.roleNames();
    }

    public String validateAlsoWithContact(final Contact contact, final String role, final String newRole) {
        return StringUtil.eitherOr(role, newRole, "role");
    }

    //endregion

    //region > edit (action)

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(position = ActionLayout.Position.PANEL)
    public ContactRole edit(
            @Parameter(maxLength = ContactRole.MaxLength.NAME, optionality = Optionality.OPTIONAL)
            final String role,
            @Parameter(maxLength = ContactRole.MaxLength.NAME, optionality = Optionality.OPTIONAL)
            final String newRole) {
        setRoleName(StringUtil.firstNonEmpty(newRole, role));
        return this;
    }

    public Set<String> choices0Edit() {
        return contactRoleRepository.roleNames();
    }
    public String default0Edit() {
        return getRoleName();
    }
    public String validateEdit(final String role, final String newRole) {
        return StringUtil.eitherOr(role, newRole, "role");
    }

    //endregion


    //region > compareTo, toString
    @Override
    public int compareTo(final ContactRole other) {
        return org.apache.isis.applib.util.ObjectContracts.compare(this, other, "contact", "contactGroup");
    }

    @Override
    public String toString() {
        return org.apache.isis.applib.util.ObjectContracts.toString(this, "contact", "contactGroup");
    }
    //endregion

    //region > injected services

    @Inject
    ContactRoleRepository contactRoleRepository;
    @Inject
    ContactGroupRepository contactGroupRepository;
    @Inject
    ContactRepository contactRepository;

    //endregion

}
