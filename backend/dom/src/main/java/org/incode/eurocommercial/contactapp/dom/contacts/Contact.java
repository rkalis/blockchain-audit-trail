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
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.incode.eurocommercial.contactapp.dom.contactable.ContactableEntity;
import org.incode.eurocommercial.contactapp.dom.group.ContactGroup;
import org.incode.eurocommercial.contactapp.dom.group.ContactGroupRepository;
import org.incode.eurocommercial.contactapp.dom.number.ContactNumber;
import org.incode.eurocommercial.contactapp.dom.number.ContactNumberRepository;
import org.incode.eurocommercial.contactapp.dom.number.ContactNumberSpec;
import org.incode.eurocommercial.contactapp.dom.role.ContactRole;
import org.incode.eurocommercial.contactapp.dom.role.ContactRoleRepository;
import org.incode.eurocommercial.contactapp.dom.util.StringUtil;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@Queries({
        @Query(
                name = "findByName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.eurocommercial.contactapp.dom.contacts.Contact "
                        + "WHERE name.matches(:regex) "),
        @Query(
                name = "findByCompany", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.eurocommercial.contactapp.dom.contacts.Contact "
                        + "WHERE company.matches(:regex) "),
        @Query(
                name = "findByEmail", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.eurocommercial.contactapp.dom.contacts.Contact "
                        + "WHERE email.matches(:regex) "),
        @Query(
                name = "listOrphanedContacts", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.eurocommercial.contactapp.dom.contacts.Contact "
                        + "WHERE contactRoles.isEmpty() ")
})
@DomainObject(
        editing = Editing.DISABLED,
        publishing = Publishing.ENABLED
)
@DomainObjectLayout(
        paged = 1000
)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class Contact extends ContactableEntity implements Comparable<Contact> {

    //region > title
    public static class MaxLength {
        private MaxLength() {
        }

        public static final int COMPANY = 50;
    }

    public String title() {
        return getName();
    }

    //endregion

    @Column(allowsNull = "true", length = MaxLength.COMPANY)
    @Property
    @Getter @Setter
    private String company;

    //region > create (action)

    @Action
    @ActionLayout(position = ActionLayout.Position.PANEL, cssClassFa = "fa fa-plus")
    public Contact create(
            @Parameter(maxLength = ContactableEntity.MaxLength.NAME)
            final String name,
            @Parameter(maxLength = MaxLength.COMPANY, optionality = Optionality.OPTIONAL)
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

    public String default1Create() {
        return getCompany();
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

    //endregion

    //region > edit (action)

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(position = ActionLayout.Position.PANEL)
    public Contact edit(
            @Parameter(maxLength = ContactableEntity.MaxLength.NAME)
            final String name,
            @Parameter(maxLength = MaxLength.COMPANY, optionality = Optionality.OPTIONAL)
            final String company,
            @Parameter(maxLength = ContactableEntity.MaxLength.EMAIL, optionality = Optionality.OPTIONAL)
            final String email,
            @Parameter(maxLength = ContactableEntity.MaxLength.NOTES, optionality = Optionality.OPTIONAL)
            @ParameterLayout(multiLine = 6)
            final String notes) {
        setName(name);
        setCompany(company);
        setEmail(email);
        setNotes(notes);
        return this;
    }

    public String validateEdit(
            final String name,
            final String company,
            final String email,
            final String notes) {
        if (!name.equals(getName()) && !contactRepository.findByName(name).isEmpty()) {
            return "This name is already in use by another contact";
        } else {
            return contactGroupRepository.findByName(name).isEmpty() ? null : "This name is already in use by a contact group";
        }
    }

    public String default0Edit() {
        return getName();
    }

    public String default1Edit() {
        return getCompany();
    }

    public String default2Edit() {
        return getEmail();
    }

    public String default3Edit() {
        return getNotes();
    }

    //endregion

    //region > delete (action)
    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(
            position = ActionLayout.Position.PANEL
    )
    public void delete() {
        contactRepository.delete(this);
    }

    //endregion

    @Persistent(mappedBy = "contact", dependentElement = "true")
    @Collection()
    @CollectionLayout(named = "Role of Contact in Groups", render = RenderType.EAGERLY)
    @Getter @Setter
    private SortedSet<ContactRole> contactRoles = new TreeSet<ContactRole>();

    //region > addContactRole (action)


    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(named = "Add")
    @MemberOrder(name = "contactRoles", sequence = "1")
    public Contact addContactRole(
            @Parameter(optionality = Optionality.MANDATORY)
            final ContactGroup contactGroup,
            @Parameter(maxLength = ContactRole.MaxLength.NAME, optionality = Optionality.OPTIONAL)
            final String role,
            @Parameter(maxLength = ContactRole.MaxLength.NAME, optionality = Optionality.OPTIONAL)
            final String newRole) {
        final String roleName = StringUtil.firstNonEmpty(newRole, role);
        contactRoleRepository.findOrCreate(this, contactGroup, roleName);
        return this;
    }

    public List<ContactGroup> choices0AddContactRole() {
        final List<ContactGroup> contactGroups = contactGroupRepository.listAll();
        final List<ContactGroup> currentGroups =
                FluentIterable
                        .from(getContactRoles())
                        .transform(ContactRole::getContactGroup)
                        .toList();
        contactGroups.removeAll(currentGroups);
        return contactGroups;
    }

    public SortedSet<String> choices1AddContactRole() {
        return contactRoleRepository.roleNames();
    }

    public String validateAddContactRole(final ContactGroup contactGroup, final String role, final String newRole) {
        return StringUtil.eitherOr(role, newRole, "role");
    }


    //endregion

    //region > removeContactRole (action)


    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(named = "Remove")
    @MemberOrder(name = "contactRoles", sequence = "2")
    public Contact removeContactRole(ContactGroup contactGroup) {
        final Optional<ContactRole> contactRoleIfAny = Iterables
                .tryFind(getContactRoles(), cn -> Objects.equal(cn.getContactGroup(), contactGroup));

        if (contactRoleIfAny.isPresent()) {
            getContactRoles().remove(contactRoleIfAny.get());
        }
        return this;
    }

    public ContactGroup default0RemoveContactRole() {
        return getContactRoles().size() == 1 ? getContactRoles().iterator().next().getContactGroup() : null;
    }

    public List<ContactGroup> choices0RemoveContactRole() {
        return Lists.transform(Lists.newArrayList(getContactRoles()), ContactRole::getContactGroup);
    }

    public String disableRemoveContactRole() {
        return getContactRoles().isEmpty() ? "No contacts to remove" : null;
    }

    //endregion

    //region > comparable impl, helpers


    @Override
    public int compareTo(final Contact o) {
        return byName.compare(this, o);
    }

    private static final Ordering<Contact> byName =
            Ordering.natural()
                    .onResultOf(nameOf());

    //endregion

    //region > injected services

    @Inject
    ContactRoleRepository contactRoleRepository;
    @Inject
    ContactGroupRepository contactGroupRepository;
    @Inject
    ContactNumberRepository contactNumberRepository;
    @Inject
    ContactRepository contactRepository;
    //endregion

}
