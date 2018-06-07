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
import java.util.Objects;
import java.util.SortedSet;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.FluentIterable;

import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.isisaddons.module.fakedata.dom.FakeDataService;

import org.incode.eurocommercial.contactapp.dom.contacts.Contact;
import org.incode.eurocommercial.contactapp.dom.contacts.ContactMenu;
import org.incode.eurocommercial.contactapp.dom.contacts.ContactRepository;
import org.incode.eurocommercial.contactapp.dom.group.ContactGroup;
import org.incode.eurocommercial.contactapp.dom.group.ContactGroupRepository;
import org.incode.eurocommercial.contactapp.dom.number.ContactNumber;
import org.incode.eurocommercial.contactapp.dom.number.ContactNumberType;
import org.incode.eurocommercial.contactapp.dom.role.ContactRole;
import org.incode.eurocommercial.contactapp.dom.role.ContactRoleRepository;
import org.incode.eurocommercial.contactapp.fixture.scenarios.demo.DemoFixture;
import org.incode.eurocommercial.contactapp.integtests.tests.ContactAppIntegTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ContactIntegTest extends ContactAppIntegTest {

    @Inject
    FixtureScripts fixtureScripts;

    @Inject
    ContactRepository contactRepository;

    @Inject
    ContactGroupRepository contactGroupRepository;

    @Inject
    ContactRoleRepository contactRoleRepository;

    @Inject
    ContactMenu contactMenu;

    @Inject
    FakeDataService fakeDataService;

    DemoFixture fs;
    Contact contact;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        // given
        fs = new DemoFixture();
        fixtureScripts.runFixtureScript(fs, null);

        contact = fs.getContacts().get(0);
        nextTransaction();

        assertThat(contact).isNotNull();
    }

    public static class Name extends ContactIntegTest {

        @Test
        public void accessible() throws Exception {
            // when
            final String name = wrap(contact).getName();
            // then
            assertThat(name).isNotNull();
        }

    }

    public static class Create extends ContactIntegTest {

        @Test
        public void happy_case() throws Exception {

            // when
            final String name = fakeDataService.name().fullName();
            final String company = fakeDataService.strings().upper(Contact.MaxLength.COMPANY);
            final String officePhoneNumber = randomPhoneNumber();
            final String mobilePhoneNumber = randomPhoneNumber();
            final String homePhoneNumber = randomPhoneNumber();
            final String email = fakeDataService.javaFaker().internet().emailAddress();

            final Contact newContact = wrap(this.contact)
                    .create(name, company, officePhoneNumber, mobilePhoneNumber, homePhoneNumber, email);
            nextTransaction();

            // then
            assertThat(contact).isNotSameAs(newContact);

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
            final String name = this.contact.getName();

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: This name is already in use by another contact");

            // when
            final String company = fakeDataService.strings().upper(Contact.MaxLength.COMPANY);
            final String officePhoneNumber = randomPhoneNumber();
            final String mobilePhoneNumber = randomPhoneNumber();
            final String homePhoneNumber = randomPhoneNumber();
            final String email = fakeDataService.javaFaker().internet().emailAddress();

            wrap(this.contact).create(name, company, officePhoneNumber, mobilePhoneNumber, homePhoneNumber, email);

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
            final String company = fakeDataService.strings().upper(Contact.MaxLength.COMPANY);
            final String officePhoneNumber = randomPhoneNumber();
            final String mobilePhoneNumber = randomPhoneNumber();
            final String homePhoneNumber = randomPhoneNumber();
            final String email = fakeDataService.javaFaker().internet().emailAddress();

            wrap(this.contact).create(existingName, company, officePhoneNumber, mobilePhoneNumber, homePhoneNumber, email);
        }

        @Test
        public void when_name_not_provided() throws Exception {
            // when
            final String company = fakeDataService.strings().upper(Contact.MaxLength.COMPANY);
            final String officePhoneNumber = randomPhoneNumber();
            final String mobilePhoneNumber = randomPhoneNumber();
            final String homePhoneNumber = randomPhoneNumber();
            final String email = fakeDataService.javaFaker().internet().emailAddress();

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: 'Name' is mandatory");
            final Contact newContact = wrap(this.contact).create(null, company, officePhoneNumber, mobilePhoneNumber, homePhoneNumber, email);
        }

    }

    public static class Edit extends ContactIntegTest {

        @Test
        public void happy_case() throws Exception {

            // when
            final String name = fakeDataService.name().fullName();
            final String company = fakeDataService.strings().upper(Contact.MaxLength.COMPANY);
            final String email = fakeDataService.javaFaker().internet().emailAddress();
            final String notes = fakeDataService.lorem().sentence(3);

            final Contact newContact = wrap(this.contact).edit(name, company, email, notes);
            nextTransaction();

            // then
            assertThat(newContact).isSameAs(this.contact);

            assertThat(newContact.getName()).isEqualTo(name);
            assertThat(newContact.getCompany()).isEqualTo(company);
            assertThat(newContact.getEmail()).isEqualTo(email);
            assertThat(newContact.getNotes()).isEqualTo(notes);
        }

        @Test
        public void name_already_in_use_by_contact() throws Exception {
            // given
            final String existingName = fs.getContacts().get(1).getName();

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: This name is already in use by another contact");

            // when
            wrap(this.contact).edit(existingName, null, null, null);
        }

        @Test
        public void name_already_in_use_by_contact_group() throws Exception {
            // given
            final String existingName = contactGroupRepository.listAll().get(0).getName();
            assertThat(existingName).isEqualTo("Amiens Property");

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: This name is already in use by a contact group");

            // when
            wrap(this.contact).edit(existingName, null, null, null);
        }

        @Test
        public void when_name_not_provided() throws Exception {
            // when
            final String name = null;

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: 'Name' is mandatory");
            wrap(this.contact).edit(name, null, null, null);
        }

        @Test
        public void without_new_name() throws Exception {
            // given
            assertThat(this.contact.getName()).isEqualTo("Bill Smith");

            // when
            wrap(this.contact).edit(this.contact.default0Edit(), "New Company", null, null);

            // then
            assertThat(this.contact.getCompany()).isEqualTo("New Company");
            // Naturally the name hasn't changed; this assertion merely ensures validateEdit
            // does not throw an exception
            assertThat(this.contact.getName()).isEqualTo("Bill Smith");
        }
    }

    public static class Delete extends ContactIntegTest {

        @Test
        public void happy_case() throws Exception {

            // given
            final List<Contact> contacts = contactRepository.listAll();

            final int sizeBefore = contacts.size();
            assertThat(sizeBefore).isGreaterThan(0);

            final Contact someContact = fakeDataService.collections().anyOf(contacts);
            final String someContactName = someContact.getName();
            nextTransaction();

            // when
            someContact.delete();
            nextTransaction();

            // then
            final List<Contact> contactsAfter = contactRepository.listAll();

            final int sizeAfter = contactsAfter.size();
            assertThat(sizeAfter).isEqualTo(sizeBefore - 1);

            assertThat(FluentIterable.from(contactsAfter).filter(
                    contact -> {
                        return Objects.equals(this.contact.getName(), someContactName);
                    }
            )).isEmpty();

        }

    }

    public static class AddNumber extends ContactIntegTest {

        String officePhoneNumber;

        @Before
        public void setUp() {
            // deliberately does not call super.setUp()

            // given
            final String name = fakeDataService.name().fullName();

            this.officePhoneNumber = randomPhoneNumber();

            this.contact = wrap(contactMenu).create(name, null, officePhoneNumber, null, null, null);
            nextTransaction();

            assertContains(this.contact.getContactNumbers(), ContactNumberType.OFFICE.title(), officePhoneNumber);
            assertThat(this.contact.getContactNumbers()).hasSize(1);
        }

        @Test
        public void add_number_with_existing_type() throws Exception {
            // when
            final String newOfficePhoneNumber = randomPhoneNumber();
            wrap(this.contact).addContactNumber(newOfficePhoneNumber, ContactNumberType.OFFICE.title(), null);
            nextTransaction();

            // then
            assertThat(this.contact.getContactNumbers()).hasSize(2);
            assertContains(this.contact.getContactNumbers(), ContactNumberType.OFFICE.title(), newOfficePhoneNumber);
            assertContains(this.contact.getContactNumbers(), ContactNumberType.OFFICE.title(), this.officePhoneNumber);
        }

        @Test
        public void add_number_with_new_type() throws Exception {
            // when
            final String newAssistantPhoneNumber = randomPhoneNumber();
            final String newType = "ASSISTANT";
            wrap(this.contact).addContactNumber(newAssistantPhoneNumber, null, newType);
            nextTransaction();

            // then
            assertThat(this.contact.getContactNumbers()).hasSize(2);
            assertContains(this.contact.getContactNumbers(), newType, newAssistantPhoneNumber);
            assertContains(this.contact.getContactNumbers(), ContactNumberType.OFFICE.title(), this.officePhoneNumber);
        }

        @Test
        public void add_number_when_already_have_number_of_any_type() throws Exception {
            // given
            final String existingNumber = this.contact.getContactNumbers().first().getNumber();
            final String currentType = this.contact.getContactNumbers().first().getType();
            final String newType = "New type";
            assertThat(this.contact.getContactNumbers().first().getType()).isNotEqualToIgnoringCase(newType);

            // when
            wrap(this.contact).addContactNumber(existingNumber, null, newType);

            // then
            assertThat(this.contact.getContactNumbers())
                    .extracting(
                            ContactNumber::getNumber,
                            ContactNumber::getType)
                    .doesNotContain(
                            Tuple.tuple(
                                    existingNumber,
                                    currentType));
            assertThat(this.contact.getContactNumbers())
                    .extracting(
                            ContactNumber::getNumber,
                            ContactNumber::getType)
                    .contains(
                            Tuple.tuple(
                                    existingNumber,
                                    newType));
        }

        @Test
        public void when_no_type_specified() throws Exception {
            // when
            final String newOfficePhoneNumber = randomPhoneNumber();

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: Must specify either an (existing) type or a new type");
            wrap(this.contact).addContactNumber(newOfficePhoneNumber, null, null);
        }

        @Test
        public void when_both_existing_type_and_new_type_specified() throws Exception {
            // when
            final String newOfficePhoneNumber = randomPhoneNumber();

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: Must specify either an (existing) type or a new type");
            wrap(this.contact).addContactNumber(newOfficePhoneNumber, ContactNumberType.OFFICE.title(), "ASSISTANT");
        }

        @Test
        public void when_no_number_provided() throws Exception {
            // when
            final String noNumber = null;

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: 'Number' is mandatory");
            wrap(this.contact).addContactNumber(noNumber, ContactNumberType.OFFICE.title(), null);
        }

        @Test
        public void invalid_number_format() throws Exception {
            // when
            final String invalidNumber = "This is an invalid number";

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: Phone number should be in form: +44 1234 5678");
            wrap(this.contact).addContactNumber(invalidNumber, ContactNumberType.OFFICE.title(), null);
        }

    }

    public static class RemoveNumber extends ContactIntegTest {

        String officePhoneNumber;
        String homePhoneNumber;

        @Before
        public void setUp() {
            // deliberately does not call super.setUp()

            // given
            final String name = fakeDataService.name().fullName();

            this.officePhoneNumber = randomPhoneNumber();
            this.homePhoneNumber = randomPhoneNumber();

            this.contact = wrap(contactMenu).create(name, null, officePhoneNumber, null, homePhoneNumber, null);
            nextTransaction();

            assertThat(this.contact.getContactNumbers()).hasSize(2);
        }

        @Test
        public void remove_number() throws Exception {

            final String existingNumber = fakeDataService.collections().anyOf(this.contact.choices0RemoveContactNumber());
            nextTransaction();

            // when
            wrap(this.contact).removeContactNumber(existingNumber);
            nextTransaction();

            // then
            assertNotContains(this.contact.getContactNumbers(), existingNumber);
        }

        @Test
        public void remove_number_when_none_exists() throws Exception {
            // given
            final int numbersBefore = this.contact.getContactNumbers()
                    .stream()
                    .map(ContactNumber::getNumber)
                    .collect(Collectors.toList())
                    .size();

            // when
            final String nonexistingNumber = "+00 0000 0000";
            wrap(this.contact).removeContactNumber(nonexistingNumber);

            // then
            assertThat(this.contact.getContactNumbers()
                    .stream()
                    .map(ContactNumber::getNumber)
                    .collect(Collectors.toList())
                    .size())
                    .isEqualTo(numbersBefore);
        }
    }

    public static class AddRole extends ContactIntegTest {

        @Test
        public void happy_case_using_existing_role_name() throws Exception {

            // given
            final int numRolesBefore = this.contact.getContactRoles().size();

            // when
            final ContactGroup contactGroup = fakeDataService.collections().anyOf(this.contact.choices0AddContactRole());
            final String existingRole = fakeDataService.collections().anyOf(this.contact.choices1AddContactRole());

            final Contact contact = wrap(this.contact).addContactRole(contactGroup, existingRole, null);
            nextTransaction();

            // then
            assertThat(contact.getContactRoles()).hasSize(numRolesBefore + 1);
            assertThat(contact.getContactRoles())
                    .extracting(
                            ContactRole::getContactGroup,
                            ContactRole::getRoleName,
                            ContactRole::getContact)
                    .contains(
                            Tuple.tuple(
                                    contactGroup,
                                    existingRole,
                                    this.contact));
        }

        @Test
        public void happy_case_using_new_role_name() throws Exception {
            // given
            final int numRolesBefore = this.contact.getContactRoles().size();

            // when
            final ContactGroup contactGroup = fakeDataService.collections().anyOf(this.contact.choices0AddContactRole());
            final String newRole = "New role";

            final Contact contact = wrap(this.contact).addContactRole(contactGroup, null, newRole);

            // then
            assertThat(contact.getContactRoles()).hasSize(numRolesBefore + 1);
            assertThat(contact.getContactRoles())
                    .extracting(
                            ContactRole::getContactGroup,
                            ContactRole::getRoleName,
                            ContactRole::getContact)
                    .contains(
                            Tuple.tuple(
                                    contactGroup,
                                    newRole,
                                    this.contact));
        }

        @Test
        public void happy_case_using_new_role_name_which_also_in_list() throws Exception {
            // given
            final int numRolesBefore = this.contact.getContactRoles().size();

            // when
            final List<ContactRole> contactRoles = contactRoleRepository.findByContact(contact);
            assertThat(contactRoles).isNotEmpty();

            ContactRole contactRole = contactRoles.get(0);
            // this role has no name provided by default
            contactRole.setRoleName("Role name");

            final String newRoleInList = contactRole.getRoleName();
            final ContactGroup contactGroup = contactRole.getContactGroup();

            final Contact contact = wrap(this.contact).addContactRole(contactGroup, null, newRoleInList);

            // then
            assertThat(contact.getContactRoles()).hasSize(numRolesBefore);
            assertThat(contact.getContactRoles())
                    .extracting(
                            ContactRole::getContactGroup,
                            ContactRole::getRoleName,
                            ContactRole::getContact)
                    .contains(
                            Tuple.tuple(
                                    contactGroup,
                                    newRoleInList,
                                    this.contact));
        }

        @Test
        public void when_no_group_specified() throws Exception {
            // when
            final ContactGroup contactGroup = null;

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: 'Contact Group' is mandatory");
            final Contact contact = wrap(this.contact).addContactRole(contactGroup, null, "new role");
        }

        @Test
        public void when_no_role_specified() throws Exception {
            // when
            final ContactGroup contactGroup = fakeDataService.collections().anyOf(this.contact.choices0AddContactRole());

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: Must specify either an (existing) role or a new role");
            final Contact contact = wrap(this.contact).addContactRole(contactGroup, null, null);
        }

        @Test
        public void when_both_existing_role_and_new_role_specified() throws Exception {
            // when
            final ContactGroup contactGroup = fakeDataService.collections().anyOf(this.contact.choices0AddContactRole());

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: Must specify either an (existing) role or a new role");
            final Contact contact = wrap(this.contact).addContactRole(contactGroup, "existing role", "new role");
        }

        @Test
        public void possible_groups_should_not_include_any_for_which_contact_already_has_a_role() throws Exception {
            // given
            final SortedSet<ContactRole> currentRoles = this.contact.getContactRoles();
            assertThat(currentRoles).hasSize(1);
            final List<ContactGroup> allGroups = contactGroupRepository.listAll();
            assertThat(allGroups).contains(currentRoles.first().getContactGroup());

            // when
            final List<ContactGroup> possibleGroups = this.contact.choices0AddContactRole();

            // then
            assertThat(possibleGroups).hasSize(allGroups.size() - currentRoles.size());
            assertThat(possibleGroups).doesNotContain(currentRoles.first().getContactGroup());
        }
    }

    public static class RemoveRole extends ContactIntegTest {

        @Test
        public void remove_role() throws Exception {

            // given
            final int contactRolesBefore = this.contact.getContactRoles().size();
            assertThat(contactRolesBefore).isGreaterThan(0);

            // when
            final ContactGroup contactGroup = fakeDataService.collections().anyOf(this.contact.choices0RemoveContactRole());
            wrap(this.contact).removeContactRole(contactGroup);
            nextTransaction();

            // then
            assertThat(contact.getContactRoles()).hasSize(contactRolesBefore - 1);

        }

        @Test
        public void remove_role_when_none_exists() throws Exception {
            // given
            final int contactRolesBefore = this.contact.getContactRoles().size();
            assertThat(contactRolesBefore).isGreaterThan(0);

            // when
            final ContactGroup contactGroup = fakeDataService.collections().anyOf(this.contact.choices0AddContactRole());
            wrap(this.contact).removeContactRole(contactGroup);
            nextTransaction();

            // then
            assertThat(this.contact.getContactRoles()).hasSize(contactRolesBefore);

        }
    }

}