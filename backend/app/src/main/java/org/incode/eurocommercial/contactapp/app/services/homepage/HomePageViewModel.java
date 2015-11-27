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
package org.incode.eurocommercial.contactapp.app.services.homepage;

import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.ViewModel;

import org.incode.eurocommercial.contactapp.dom.contacts.ContactRepository;
import org.incode.eurocommercial.contactapp.dom.country.Country;
import org.incode.eurocommercial.contactapp.dom.group.ContactGroup;
import org.incode.eurocommercial.contactapp.dom.group.ContactGroupRepository;

@ViewModel
public class HomePageViewModel {

    public String title() {
        return "Contact Groups";
    }


    @Collection(editing = Editing.DISABLED)
    @CollectionLayout(paged=200)
    @org.apache.isis.applib.annotation.HomePage
    public List<ContactGroup> getGroups() {
        return contactGroupRepository.listAll();
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(named = "Create", cssClassFa = "fa fa-plus")
    @MemberOrder(name = "groups", sequence = "1")
    public HomePageViewModel newContactGroup(
            final Country country,
            final String name) {
        contactGroupRepository.findOrCreate(country, name);
        return this;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(named = "Delete")
    @MemberOrder(name = "groups", sequence = "2")
    public HomePageViewModel deleteContactGroup(
            final ContactGroup contactGroup,
            @ParameterLayout(named = "This will also delete all Contact Roles connected to it, do you wish to proceed?")
            final boolean delete) {
        if (delete) {
            contactGroupRepository.delete(contactGroup);
        }
        return this;
    }

    public String validateDeleteContactGroup(final ContactGroup contactGroup, final boolean delete) {
        return delete ? null : "You have to agree";
    }

    public List<ContactGroup> choices0DeleteContactGroup() {
        return contactGroupRepository.listAll();
    }
    public ContactGroup default0DeleteContactGroup() {
        final List<ContactGroup> choices = choices0DeleteContactGroup();
        return choices.isEmpty()? null: choices.get(0);
    }


    public String disableDeleteContactGroup() {
        return choices0DeleteContactGroup().isEmpty()? "No contact groups": null;
    }

    @javax.inject.Inject
    ContactGroupRepository contactGroupRepository;
    @javax.inject.Inject
    ContactRepository contactRepository;


}
