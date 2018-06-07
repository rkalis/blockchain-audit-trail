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
package org.incode.eurocommercial.contactapp.integtests.tests.contact;

import java.util.List;
import java.util.SortedSet;

import javax.inject.Inject;
import javax.jdo.JDOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.incode.eurocommercial.contactapp.dom.contacts.Contact;
import org.incode.eurocommercial.contactapp.dom.contacts.ContactMenu;
import org.incode.eurocommercial.contactapp.dom.country.CountryRepository;
import org.incode.eurocommercial.contactapp.dom.group.ContactGroup;
import org.incode.eurocommercial.contactapp.dom.group.ContactGroupRepository;
import org.incode.eurocommercial.contactapp.dom.number.ContactNumberType;
import org.incode.eurocommercial.contactapp.dom.role.ContactRole;
import org.incode.eurocommercial.contactapp.fixture.scenarios.demo.DemoFixture;
import org.incode.eurocommercial.contactapp.integtests.tests.ContactAppIntegTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ContactMenuIntegTest extends ContactAppIntegTest {

    @Inject
    FixtureScripts fixtureScripts;

    @Inject
    ContactMenu contactMenu;

    @Inject
    ContactGroupRepository contactGroupRepository;

    @Inject
    CountryRepository countryRepository;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    DemoFixture fs;
    Contact contact;

    @Before
    public void setUp() throws Exception {
        // given
        fs = new DemoFixture();
        fixtureScripts.runFixtureScript(fs, null);

        contact = fs.getContacts().get(0);
        nextTransaction();

        assertThat(contact).isNotNull();
    }

    public static class Find extends ContactMenuIntegTest {

        @Test
        public void match_on_name() throws Exception {
            // when
            final List<Contact> result = contactMenu.find(this.contact.getName());

            // then
            assertThat(result).hasSize(1);
            assertThat(result).contains(this.contact);
            assertThat(result.get(0).getName()).isEqualTo(this.contact.getName());
        }

        @Test
        public void match_on_company() throws Exception {
            // when
            final List<Contact> result = contactMenu.find(this.contact.getCompany());

            // then
            assertThat(result).isNotEmpty();
            assertThat(result).contains(this.contact);
        }

        @Test
        public void match_on_email() throws Exception {
            // when
            final List<Contact> result = contactMenu.find(this.contact.getEmail());

            // then
            assertThat(result).hasSize(1);
            assertThat(result).contains(this.contact);
            assertThat(result.get(0).getName()).isEqualTo(this.contact.getName());
        }

        @Test
        public void no_match_on_any() throws Exception {
            // given
            final String query = "This will yield zero results";

            // when
            final List<Contact> result = contactMenu.find(query);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        public void no_query_string_provided() throws Exception {
            // given
            final String query = null;

            // then
            thrown.expect(JDOException.class);
            thrown.expectMessage("Incorrect arguments for String.matches(StringExpression)");

            // when
            contactMenu.find(query);
        }
    }

    public static class FindByGroup extends ContactMenuIntegTest {

        @Test
        public void matches() throws Exception {
            // given
            final SortedSet<ContactRole> roles = this.contact.getContactRoles();
            assertThat(roles).isNotEmpty();

            // when
            final List<Contact> result = contactMenu.findByGroup(roles.first().getContactGroup());

            // then
            assertThat(result).isNotEmpty();
        }

        @Test
        public void no_match() throws Exception {
            // given
            final ContactGroup contactGroup = contactGroupRepository.create(countryRepository.listAll().get(0), "No match group");

            // when
            final List<Contact> result = contactMenu.findByGroup(contactGroup);

            // then
            assertThat(result).isEmpty();
        }

        @Ignore("See ELI-92")
        @Test
        public void no_group_specified() throws Exception {
            // given
            final ContactGroup contactGroup = null;

            // then
            thrown.expect(JDOException.class);

            // when
            contactMenu.findByGroup(contactGroup);
        }

    }

    public static class FindByRole extends ContactMenuIntegTest {

        @Test
        public void matches() throws Exception {
            // given
            final SortedSet<ContactRole> contactRoles = this.contact.getContactRoles();
            assertThat(contactRoles).isNotEmpty();

            // need to add role name
            final String roleName = "Role name";
            contactRoles.first().setRoleName(roleName);

            // when
            final List<Contact> result = contactMenu.findByRole(roleName);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result).contains(this.contact);
        }

        @Test
        public void no_match() throws Exception {
            // given
            final String roleName = "No match role name";

            // when
            final List<Contact> result = contactMenu.findByRole(roleName);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        public void no_role_specified() throws Exception {
            // given
            final String roleName = null;

            // then
            thrown.expect(JDOException.class);
            thrown.expectMessage("Incorrect arguments for String.matches(StringExpression)");

            // when
            contactMenu.findByRole(roleName);
        }

    }

    public static class ListAll extends ContactMenuIntegTest {

        @Test
        public void happy_case() throws Exception {

            // when
            final List<Contact> returned = wrap(contactMenu).listAll();

            // then
            final List<Contact> contacts = fs.getContacts();
            assertThat(returned).hasSize(contacts.size());

            assertThat(returned).containsAll(fs.getContacts());
            assertThat(fs.getContacts()).containsAll(returned);
        }

    }

    public static class Create extends ContactMenuIntegTest {

        @Test
        public void happy_case_with_minimal_info_provided() throws Exception {

            // when
            final String name = fakeDataService.name().fullName();
            final Contact newContact = wrap(contactMenu).create(name, null, null, null, null, null);
            nextTransaction();

            // then
            assertThat(newContact.getName()).isEqualTo(name);
            assertThat(newContact.getCompany()).isNull();
            assertThat(newContact.getEmail()).isNull();
            assertThat(newContact.getNotes()).isNull();
            assertThat(newContact.getContactNumbers()).isEmpty();
            assertThat(newContact.getContactRoles()).isEmpty();
        }

        @Test
        public void happy_case_with_all_info_provided() throws Exception {

            // when
            final String name = fakeDataService.name().fullName();
            final String company = fakeDataService.strings().upper(Contact.MaxLength.COMPANY);
            final String officePhoneNumber = randomPhoneNumber();
            final String mobilePhoneNumber = randomPhoneNumber();
            final String homePhoneNumber = randomPhoneNumber();
            final String email = fakeDataService.javaFaker().internet().emailAddress();

            final Contact newContact = wrap(contactMenu)
                    .create(name, company, officePhoneNumber, mobilePhoneNumber, homePhoneNumber, email);
            nextTransaction();

            // then
            assertThat(newContact.getName()).isEqualTo(name);
            assertThat(newContact.getCompany()).isEqualTo(company);
            assertThat(newContact.getEmail()).isEqualTo(email);

            assertThat(newContact.getContactNumbers()).hasSize(3);

            assertContains(newContact.getContactNumbers(), ContactNumberType.OFFICE.title(), officePhoneNumber);
            assertContains(newContact.getContactNumbers(), ContactNumberType.MOBILE.title(), mobilePhoneNumber);
            assertContains(newContact.getContactNumbers(), ContactNumberType.HOME.title(), homePhoneNumber);

            assertThat(newContact.getContactRoles()).isEmpty();

            assertThat(newContact.getNotes()).isNull();
        }

        @Test
        public void name_already_in_use_by_contact() throws Exception {
            // given
            final String existingName = this.contact.getName();
            assertThat(existingName).isNotNull();

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: This name is already in use by another contact");

            // when
            wrap(contactMenu).create(existingName, null, null, null, null, null);
        }

        @Test
        public void name_already_in_use_by_contact_group() throws Exception {
            // given
            final String existingName = contactGroupRepository.listAll().get(0).getName();
            assertThat(existingName).isNotEmpty();

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: This name is already in use by a contact group");

            // when
            wrap(contactMenu).create(existingName, null, null, null, null, null);
        }

        @Test
        public void no_name_specified() throws Exception {
            // given
            final String name = null;

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: 'Name' is mandatory");

            // when
            wrap(contactMenu).create(name, null, null, null, null, null);
        }

    }

}