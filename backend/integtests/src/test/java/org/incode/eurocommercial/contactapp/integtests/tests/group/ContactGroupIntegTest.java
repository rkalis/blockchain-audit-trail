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
package org.incode.eurocommercial.contactapp.integtests.tests.group;

import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.wrapper.DisabledException;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.isisaddons.module.fakedata.dom.FakeDataService;

import org.incode.eurocommercial.contactapp.dom.contactable.ContactableEntity;
import org.incode.eurocommercial.contactapp.dom.contacts.Contact;
import org.incode.eurocommercial.contactapp.dom.contacts.ContactRepository;
import org.incode.eurocommercial.contactapp.dom.country.Country;
import org.incode.eurocommercial.contactapp.dom.country.CountryRepository;
import org.incode.eurocommercial.contactapp.dom.group.ContactGroup;
import org.incode.eurocommercial.contactapp.dom.group.ContactGroupRepository;
import org.incode.eurocommercial.contactapp.dom.number.ContactNumber;
import org.incode.eurocommercial.contactapp.dom.number.ContactNumberType;
import org.incode.eurocommercial.contactapp.dom.role.ContactRole;
import org.incode.eurocommercial.contactapp.dom.role.ContactRoleRepository;
import org.incode.eurocommercial.contactapp.fixture.scenarios.demo.DemoFixture;
import org.incode.eurocommercial.contactapp.integtests.tests.ContactAppIntegTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ContactGroupIntegTest extends ContactAppIntegTest {

    @Inject
    FixtureScripts fixtureScripts;

    @Inject
    CountryRepository countryRepository;

    @Inject
    ContactRepository contactRepository;

    @Inject
    ContactGroupRepository contactGroupRepository;

    @Inject
    ContactRoleRepository contactRoleRepository;

    @Inject
    FakeDataService fakeDataService;

    DemoFixture fs;

    ContactGroup contactGroup;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        // given
        fs = new DemoFixture();
        fixtureScripts.runFixtureScript(fs, null);
        nextTransaction();

        contactGroup = contactGroupRepository.listAll().get(0);
    }

    public static class Create extends ContactGroupIntegTest {

        @Test
        public void happy_case() throws Exception {

            // when
            final Country country = fakeDataService.collections().anyOf(countryRepository.listAll());
            final String name = fakeDataService.strings().upper(ContactableEntity.MaxLength.NAME);

            final ContactGroup newContactGroup = wrap(this.contactGroup).create(country, name);
            nextTransaction();

            // then
            assertThat(newContactGroup).isNotSameAs(this.contactGroup);
            assertThat(newContactGroup.getCountry()).isEqualTo(country);
            assertThat(newContactGroup.getName()).isEqualTo(name);

        }

        @Test
        public void name_already_in_use_by_contact() throws Exception {
            // given
            final Country country = fakeDataService.collections().anyOf(countryRepository.listAll());
            final String existingName = contactRepository.listAll().get(0).getName();
            assertThat(existingName).isNotEmpty();

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: This name is already in use by a contact");

            // when
            wrap(this.contactGroup).create(country, existingName);
        }

        @Test
        public void name_already_in_use_by_contact_group() throws Exception {
            // given
            final String existingName = "This name already exists";
            final Country country = fakeDataService.collections().anyOf(countryRepository.listAll());
            final ContactGroup contactGroup = contactGroupRepository.listAll().get(0);
            // Ensure that contact group actually has a name
            contactGroup.setName(existingName);

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: This name is already in use by another contact group");

            // when
            wrap(this.contactGroup).create(country, existingName);
        }

        @Test
        public void when_name_not_provided() throws Exception {
            // given
            final String name = null;
            final Country country = fakeDataService.collections().anyOf(countryRepository.listAll());

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: 'Name' is mandatory");

            // when
            wrap(this.contactGroup).create(country, name);
        }

    }

    public static class Edit extends ContactGroupIntegTest {

        @Test
        public void happy_case() throws Exception {

            // when
            final String name = fakeDataService.strings().upper(ContactableEntity.MaxLength.NAME);
            final String address = fakeDataService.addresses().streetAddressWithSecondary();
            final String email = fakeDataService.javaFaker().internet().emailAddress();
            final String notes = fakeDataService.lorem().paragraph(3);

            final ContactableEntity returned = wrap(this.contactGroup).edit(name, address, email, notes);
            nextTransaction();

            // then
            assertThat(returned).isSameAs(this.contactGroup);
            assertThat(this.contactGroup.getName()).isEqualTo(name);
            assertThat(this.contactGroup.getAddress()).isEqualTo(address);
            assertThat(this.contactGroup.getEmail()).isEqualTo(email);
            assertThat(this.contactGroup.getNotes()).isEqualTo(notes);

        }

        @Test
        public void name_already_in_use_by_contact() throws Exception {
            // given
            final String existingName = contactRepository.listAll().get(0).getName();
            assertThat(this.contactGroup.getName()).isNotEmpty();
            assertThat(this.contactGroup.getName()).isNotEqualToIgnoringCase(existingName);

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: This name is already in use by a contact");

            // when
            wrap(this.contactGroup).edit(existingName, null, null, null);
        }

        @Test
        public void name_already_in_use_by_contact_group() throws Exception {
            // given
            final String existingName = contactGroupRepository.listAll().get(1).getName();
            assertThat(existingName).isNotEqualTo(this.contactGroup.getName());

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: This name is already in use by another contact group");

            // when
            wrap(this.contactGroup).edit(existingName, null, null, null);
        }

        @Test
        public void when_name_not_provided() throws Exception {
            // given
            final String name = null;

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: 'Name' is mandatory");

            // when
            wrap(this.contactGroup).edit(name, null, null, null);
        }

        @Test
        public void without_new_name() throws Exception {
            // given
            assertThat(this.contactGroup.getName()).isEqualTo("Amiens Property");

            // when
            wrap(this.contactGroup).edit(this.contactGroup.default0Edit(), "New Address", null, null);

            // then
            assertThat(this.contactGroup.getAddress()).isEqualTo("New Address");
            // Naturally the name hasn't changed; this assertion merely ensures validateEdit
            // does not throw an exception
            assertThat(this.contactGroup.getName()).isEqualTo("Amiens Property");
        }
    }

    public static class Delete extends ContactGroupIntegTest {

        @Test
        public void happy_case() throws Exception {
            // given
            final List<ContactGroup> contactGroups = contactGroupRepository.listAll();
            final int sizeBefore = contactGroups.size();
            assertThat(sizeBefore).isNotZero();

            final ContactGroup someContactGroup = fakeDataService.collections().anyOf(contactGroups);

            // when
            someContactGroup.delete(true);
            nextTransaction();

            // then
            final List<ContactGroup> contactGroupsAfter = contactGroupRepository.listAll();
            final int sizeAfter = contactGroupsAfter.size();

            assertThat(sizeAfter).isEqualTo(sizeBefore - 1);
        }


    }

    public static class AddNumber extends ContactGroupIntegTest {

        @Test
        public void add_number_with_existing_type() throws Exception {
            // given
            final String newOfficePhoneNumber = randomPhoneNumber();
            final String type = ContactNumberType.OFFICE.title();

            for (ContactNumber contactNumber : this.contactGroup.getContactNumbers()) {
                assertThat(contactNumber).isNotEqualTo(newOfficePhoneNumber);
            }

            // when
            wrap(this.contactGroup).addContactNumber(newOfficePhoneNumber, type, null);

            // then
            assertThat(this.contactGroup.getContactNumbers())
                    .extracting(ContactNumber::getNumber)
                    .contains(newOfficePhoneNumber);
        }

        @Test
        public void add_number_with_new_type() throws Exception {
            // given
            final String newOfficePhoneNumber = randomPhoneNumber();
            final String newType = "New number type";

            for (ContactNumber contactNumber : this.contactGroup.getContactNumbers()) {
                assertThat(contactNumber).isNotEqualTo(newOfficePhoneNumber);
            }

            // when
            wrap(this.contactGroup).addContactNumber(newOfficePhoneNumber, null, newType);

            // then
            assertThat(this.contactGroup.getContactNumbers())
                    .extracting(ContactNumber::getNumber)
                    .contains(newOfficePhoneNumber);
        }

        @Test
        public void add_number_when_already_have_number_of_any_type() throws Exception {
            // given
            final String officePhoneNumber = randomPhoneNumber();
            wrap(this.contactGroup).addContactNumber(officePhoneNumber, ContactNumberType.OFFICE.title(), null);
            assertThat(this.contactGroup.getContactNumbers())
                    .extracting(ContactNumber::getNumber)
                    .contains(officePhoneNumber);
            final String newType = "New type";

            // when
            wrap(this.contactGroup).addContactNumber(officePhoneNumber, null, newType);

            // then
            assertThat(this.contactGroup.getContactNumbers())
                    .extracting(
                            ContactNumber::getNumber,
                            ContactNumber::getType)
                    .doesNotContain(
                            Tuple.tuple(
                                    officePhoneNumber,
                                    ContactNumberType.OFFICE.title()));
            assertThat(this.contactGroup.getContactNumbers())
                    .extracting(
                            ContactNumber::getNumber,
                            ContactNumber::getType)
                    .contains(
                            Tuple.tuple(
                                    officePhoneNumber,
                                    newType));
        }

        @Test
        public void when_no_type_specified() throws Exception {
            // given
            final String newOfficePhoneNumber = randomPhoneNumber();

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: Must specify either an (existing) type or a new type");

            // when
            wrap(this.contactGroup).addContactNumber(newOfficePhoneNumber, null, null);
        }

        @Test
        public void when_both_existing_type_and_new_type_specified() throws Exception {
            // given
            final String newOfficePhoneNumber = randomPhoneNumber();

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: Must specify either an (existing) type or a new type");

            // when
            wrap(this.contactGroup).addContactNumber(newOfficePhoneNumber, ContactNumberType.OFFICE.title(), "ASSISTANT");
        }

        @Test
        public void when_no_number_provided() throws Exception {
            // given
            final String noNumber = null;

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: 'Number' is mandatory");

            // when
            wrap(this.contactGroup).addContactNumber(noNumber, ContactNumberType.OFFICE.title(), null);
        }

        @Test
        public void invalid_number_format() throws Exception {
            // given
            final String invalidNumber = "This is an invalid number";

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: Phone number should be in form: +44 1234 5678");

            // when
            wrap(this.contactGroup).addContactNumber(invalidNumber, ContactNumberType.OFFICE.title(), null);
        }

    }

    public static class RemoveNumber extends ContactGroupIntegTest {

        @Test
        public void remove_number() throws Exception {
            // given
            final String number = randomPhoneNumber();

            // ensure there actually is a number
            wrap(this.contactGroup).addContactNumber(number, ContactNumberType.OFFICE.title(), null);

            assertThat(this.contactGroup.getContactNumbers())
                    .extracting(ContactNumber::getNumber)
                    .contains(number);
            nextTransaction();

            // when
            wrap(this.contactGroup).removeContactNumber(number);
            nextTransaction();

            // then
            assertThat(this.contactGroup.getContactNumbers())
                    .extracting(ContactNumber::getNumber)
                    .doesNotContain(number);
        }

        @Test
        public void remove_number_when_none_exists() throws Exception {
            // need to add a number so remove is not disabled
            wrap(this.contactGroup).addContactNumber(randomPhoneNumber(), ContactNumberType.OFFICE.title(), null);

            // given
            final int numbersBefore = this.contactGroup.getContactNumbers()
                    .stream()
                    .map(ContactNumber::getNumber)
                    .collect(Collectors.toList())
                    .size();

            // when
            final String nonexistingNumber = "+00 0000 0000";
            wrap(this.contactGroup).removeContactNumber(nonexistingNumber);

            // then
            assertThat(this.contactGroup.getContactNumbers()
                    .stream()
                    .map(ContactNumber::getNumber)
                    .collect(Collectors.toList())
                    .size())
                    .isEqualTo(numbersBefore);
        }
    }

    public static class AddRole extends ContactGroupIntegTest {

        @Test
        public void happy_case_using_existing_role_name() throws Exception {
            // given
            final int numRolesBefore = this.contactGroup.getContactRoles().size();

            // when
            final String existingRole = fakeDataService.collections().anyOf(this.contactGroup.choices1AddContactRole());
            final Contact contact = fakeDataService.collections().anyOf(this.contactGroup.choices0AddContactRole());
            assertThat(existingRole).isNotEmpty();

            final ContactGroup contactGroup = wrap(this.contactGroup).addContactRole(contact, existingRole, null);
            nextTransaction();

            // then
            assertThat(contactGroup.getContactRoles()).hasSize(numRolesBefore + 1);
            assertThat(contactGroup.getContactRoles())
                    .extracting(
                            ContactRole::getContactGroup,
                            ContactRole::getRoleName,
                            ContactRole::getContact)
                    .contains(
                            Tuple.tuple(
                                    contactGroup,
                                    existingRole,
                                    contact));
        }

        @Test
        public void happy_case_using_new_role_name() throws Exception {
            // given
            final int numRolesBefore = this.contactGroup.getContactRoles().size();

            // when
            final String newRole = "New role";
            final Contact contact = fakeDataService.collections().anyOf(this.contactGroup.choices0AddContactRole());
            assertThat(newRole).isNotEmpty();

            final ContactGroup contactGroup = wrap(this.contactGroup).addContactRole(contact, null, newRole);
            nextTransaction();

            // then
            assertThat(contactGroup.getContactRoles()).hasSize(numRolesBefore + 1);
            assertThat(contactGroup.getContactRoles())
                    .extracting(
                            ContactRole::getContactGroup,
                            ContactRole::getRoleName,
                            ContactRole::getContact)
                    .contains(
                            Tuple.tuple(
                                    contactGroup,
                                    newRole,
                                    contact));
        }

        @Test
        public void happy_case_using_new_role_name_which_also_in_list() throws Exception {
            // given
            final int numRolesBefore = this.contactGroup.getContactRoles().size();

            // when
            final List<ContactRole> contactRoles = contactRoleRepository.findByGroup(this.contactGroup);
            assertThat(contactRoles).isNotEmpty();

            ContactRole contactRole = contactRoles.get(0);
            // this role has no name provided by default
            contactRole.setRoleName("Role name");

            final String newRoleInList = contactRole.getRoleName();
            final Contact contact = contactRole.getContact();

            final ContactGroup contactGroup = wrap(this.contactGroup).addContactRole(contact, null, newRoleInList);

            // then
            assertThat(contactGroup.getContactRoles()).hasSize(numRolesBefore);
            assertThat(contactGroup.getContactRoles())
                    .extracting(
                            ContactRole::getContactGroup,
                            ContactRole::getRoleName,
                            ContactRole::getContact)
                    .contains(Tuple.tuple(
                            contactGroup,
                            newRoleInList,
                            contact));
        }

        @Test
        public void when_no_contact_specified() throws Exception {
            // given
            final Contact contact = null;

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: 'Contact' is mandatory");

            // when
            wrap(this.contactGroup).addContactRole(contact, null, "new role");
        }

        @Test
        public void when_no_role_specified() throws Exception {
            // given
            final Contact contact = fakeDataService.collections().anyOf(this.contactGroup.choices0AddContactRole());

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: Must specify either an (existing) role or a new role");

            // when
            wrap(this.contactGroup).addContactRole(contact, null, null);
        }

        @Test
        public void when_both_existing_role_and_new_role_specified() throws Exception {
            // given
            final Contact contact = fakeDataService.collections().anyOf(this.contactGroup.choices0AddContactRole());

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: Must specify either an (existing) role or a new role");

            // when
            wrap(this.contactGroup).addContactRole(contact, ContactNumberType.OFFICE.title(), "new role");
        }

        @Test
        public void possible_contacts_should_not_include_any_for_which_contact_already_has_a_role() throws Exception {
            // given
            final SortedSet<ContactRole> currentRoles = this.contactGroup.getContactRoles();
            assertThat(currentRoles).hasSize(5);

            final List<Contact> allContacts = contactRepository.listAll();
            assertThat(allContacts).contains(currentRoles.first().getContact());

            // when
            final List<Contact> possibleContacts = this.contactGroup.choices0AddContactRole();

            // then
            assertThat(possibleContacts).hasSize(allContacts.size() - currentRoles.size());
            assertThat(possibleContacts).doesNotContain(currentRoles.first().getContact());
        }
    }

    public static class RemoveRole extends ContactGroupIntegTest {

        @Test
        public void remove_role() throws Exception {
            // given
            final int contactRolesBefore = this.contactGroup.getContactRoles().size();
            assertThat(contactRolesBefore).isNotZero();

            // when
            final Contact contact = fakeDataService.collections().anyOf(this.contactGroup.choices0RemoveContactRole());
            wrap(this.contactGroup).removeContactRole(contact);
            nextTransaction();

            // then
            assertThat(this.contactGroup.getContactRoles()).hasSize(contactRolesBefore - 1);
        }

        @Test
        public void remove_role_when_none_exists() throws Exception {
            // given
            final int contactRolesBefore = this.contactGroup.getContactRoles().size();
            assertThat(contactRolesBefore).isNotZero();

            // when
            final Contact contact = fakeDataService.collections().anyOf(this.contactGroup.choices0AddContactRole());
            wrap(this.contactGroup).removeContactRole(contact);
            nextTransaction();

            // then
            assertThat(this.contactGroup.getContactRoles()).hasSize(contactRolesBefore);
        }
    }

}