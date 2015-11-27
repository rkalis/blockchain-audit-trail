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
package org.incode.eurocommercial.contactapp.fixture.scenarios.demo;

import java.util.Collections;

import org.apache.commons.lang.StringUtils;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.eurocommercial.contactapp.dom.contacts.Contact;
import org.incode.eurocommercial.contactapp.dom.contacts.ContactRepository;
import org.incode.eurocommercial.contactapp.dom.country.Country;
import org.incode.eurocommercial.contactapp.dom.country.CountryRepository;
import org.incode.eurocommercial.contactapp.dom.group.ContactGroup;
import org.incode.eurocommercial.contactapp.dom.group.ContactGroupRepository;
import org.incode.eurocommercial.contactapp.dom.number.ContactNumberType;

import lombok.Getter;
import lombok.Setter;

public class ContactImport implements org.isisaddons.module.excel.dom.ExcelFixtureRowHandler {

    @Getter @Setter
    private String country;
    @Getter @Setter
    private String address;
    @Getter @Setter
    private String group;
    @Getter @Setter
    private String company;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private String office;
    @Getter @Setter
    private String mobile;
    @Getter @Setter
    private String home;
    @Getter @Setter
    private String email;
    @Getter @Setter
    private String role;
    @Getter @Setter
    private String note;
    @Getter @Setter
    private Integer disorder;

    @Override
    public java.util.List<Object> handleRow(
            final FixtureScript.ExecutionContext executionContext,
            final org.isisaddons.module.excel.dom.ExcelFixture excelFixture,
            final Object previousRow) {

        country = StringUtils.trimToNull(country);
        address = StringUtils.trimToNull(address);
        group   = StringUtils.trimToNull(group);
        company = StringUtils.trimToNull(company);
        name    = StringUtils.trimToNull(name);
        office  = StringUtils.trimToNull(office);
        mobile  = StringUtils.trimToNull(mobile);
        home    = StringUtils.trimToNull(home);
        email   = StringUtils.trimToNull(email);
        role    = StringUtils.trimToNull(role);
        note    = StringUtils.trimToNull(note);

        final ContactImport previousContactRow = (ContactImport) previousRow;
        if(previousContactRow != null) {
            if(country == null) country = previousContactRow.getCountry();
            if(group   == null) group   = previousContactRow.getGroup();
        }

        if(office != null) office = office.replace("(0)", "");
        if(mobile != null) mobile = mobile.replace("(0)", "");
        if(home   != null) home   = home  .replace("(0)", "");

        Country country = countryRepository.findOrCreate(this.country);
        ContactGroup contactGroup = contactGroupRepository.findOrCreate(country, group);

        if(address  != null) contactGroup.setAddress(address);
        if(disorder != null) {
            int displayOrder = disorder;
            contactGroup.setDisplayOrder(displayOrder);
        }
        Contact contact = null;
        if(name == null) {
            if(office != null) contactGroup.addContactNumber(office, ContactNumberType.OFFICE.title(), null);
            if(mobile != null) contactGroup.addContactNumber(mobile, ContactNumberType.MOBILE.title(), null);
            if(home   != null) contactGroup.addContactNumber(  home, ContactNumberType.  HOME.title(), null);
            if(email  != null) contactGroup.setEmail(email);
        }
        else {
            contact = contactRepository.findOrCreate(name, company, email, note, office, mobile, home);
            contact.addContactRole(contactGroup, role, null);

            if(company != null && company.equals("Eurocommercial")) {
                Country globalCountry = countryRepository.findOrCreate("Global");
                ContactGroup ecpCompanyGroup = contactGroupRepository.findOrCreate(globalCountry, "ECP Company");
                contact.addContactRole(ecpCompanyGroup, "Eurocommercial", null);
            }
        }
        executionContext.addResult(excelFixture, contact);

        return Collections.singletonList(contact);
    }

    @javax.inject.Inject
    CountryRepository countryRepository;
    @javax.inject.Inject
    ContactRepository contactRepository;
    @javax.inject.Inject
    ContactGroupRepository contactGroupRepository;

}
