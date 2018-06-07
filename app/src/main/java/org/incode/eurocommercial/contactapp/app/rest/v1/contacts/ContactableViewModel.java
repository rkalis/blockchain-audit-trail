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

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.isis.applib.DomainObjectContainer;

import org.incode.eurocommercial.contactapp.app.rest.ViewModelWithUnderlying;
import org.incode.eurocommercial.contactapp.app.rest.v1.number.ContactNumberViewModel;
import org.incode.eurocommercial.contactapp.app.rest.v1.role.ContactRoleViewModel;
import org.incode.eurocommercial.contactapp.dom.contactable.ContactableEntity;
import org.incode.eurocommercial.contactapp.dom.contacts.Contact;
import org.incode.eurocommercial.contactapp.dom.country.Country;
import org.incode.eurocommercial.contactapp.dom.group.ContactGroup;
import org.incode.eurocommercial.contactapp.dom.role.ContactRole;

public class ContactableViewModel extends ViewModelWithUnderlying<ContactableEntity> {

    public enum Type {
        CONTACT,
        CONTACT_GROUP
    }

    public static class Functions {
        private Functions(){}
    }
    public static Function<Contact, ContactableViewModel> createForContact(final DomainObjectContainer container) {
        return new Function<Contact, ContactableViewModel>() {
            @Nullable @Override public ContactableViewModel apply(@Nullable final Contact input) {
                return input != null ? container.injectServicesInto(new ContactableViewModel(input)): null;
            }
        };
    }

    public static Function<ContactGroup, ContactableViewModel> createForGroup(final DomainObjectContainer container) {
        return new Function<ContactGroup, ContactableViewModel>() {
            @Nullable @Override public ContactableViewModel apply(@Nullable final ContactGroup input) {
                return input != null? container.injectServicesInto(new ContactableViewModel(input)): null;
            }
        };
    }

    private Contact contact() {
        return getType() == Type.CONTACT? (Contact) underlying : null;
    }

    private ContactGroup contactGroup() {
        return getType() == Type.CONTACT_GROUP? (ContactGroup) underlying : null;
    }

    public ContactableViewModel() {
    }

    public ContactableViewModel(Contact contact) {
        this.underlying = contact;
    }

    public ContactableViewModel(ContactGroup contactGroup) {
        this.underlying = contactGroup;
    }

    public Type getType() {
        return this.underlying instanceof Contact? Type.CONTACT: Type.CONTACT_GROUP;
    }

    public String getName() {
        final String name = underlying.getName();
        // remove white space and &nbsp;
        return name != null
                ?name.replace(String.valueOf((char) 160), " ").trim()
                :null;
    }

    /**
     * Only populated for {@link #getType()} of {@link Type#CONTACT_GROUP}.
     */
    public Integer getDisplayOrder() {
        return getType() == Type.CONTACT_GROUP? contactGroup().getDisplayOrder(): null;
    }

    public String getEmail() {
        return underlying.getEmail();
    }

    public String getNotes() {
        return underlying.getNotes();
    }

    public List<ContactNumberViewModel> getContactNumbers() {
        return Lists.newArrayList(
                Iterables.transform(underlying.getContactNumbers(), ContactNumberViewModel.create(container))
        );
    }

    /**
     * For searching by the filter bar.
     * @return
     */
    @XmlTransient
    public String getContactRoleNames() {
        return Joiner.on(";").join(
                FluentIterable.from(getContactRoles())
                .transform(ContactRoleViewModel.nameOf())
                .filter(Predicates.notNull()));
    }

    /**
     * For {@link Type#CONTACT contacts}, returns the roles they have (in various groups)
     * For {@link Type#CONTACT_GROUP contact groups}, returns the roles that different contacts play within that group.
     *
     * @return
     */
    @XmlTransient
    public List<ContactRoleViewModel> getContactRoles() {
        final Collection<ContactRole> contactRoles =
                getType() == Type.CONTACT_GROUP
                    ? contactGroup().getContactRoles()
                    : contact().getContactRoles();
        return Lists.newArrayList(
                Iterables.transform(
                    contactRoles,
                    ContactRoleViewModel.create(container)));
    }

    /**
     * Only populated for {@link #getType()} of {@link Type#CONTACT}.
     */
    public String getCompany() {
        if (getType() == Type.CONTACT_GROUP) {
            return null;
        }
        return contact().getCompany();
    }

    /**
     * Only populated for {@link #getType()} of {@link Type#CONTACT_GROUP}.
     */
    public String getCountry() {
        if(getType() == Type.CONTACT) return null;
        final Country country = contactGroup().getCountry();
        return country != null? container.titleOf(country): null;
    }

    /**
     * Only populated for {@link #getType()} of {@link Type#CONTACT_GROUP}.
     */
    public String getAddress() {
        if(getType() == Type.CONTACT) return null;
        return contactGroup().getAddress();
    }

    static String firstNameFrom(final String name) {
        final int i = name.lastIndexOf(" ");
        return i != -1? name.substring(0, i): "";
    }

    static String lastNameFrom(final String name) {
        final int i = name.lastIndexOf(" ");
        return i != -1? name.substring(i+1): name;
    }



}
