/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.incode.eurocommercial.contactapp.integtests.tests.role;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.incode.eurocommercial.contactapp.dom.contacts.Contact;
import org.incode.eurocommercial.contactapp.dom.contacts.ContactRepository;
import org.incode.eurocommercial.contactapp.dom.group.ContactGroup;
import org.incode.eurocommercial.contactapp.dom.group.ContactGroupRepository;
import org.incode.eurocommercial.contactapp.dom.role.ContactRole;
import org.incode.eurocommercial.contactapp.dom.role.ContactRoleRepository;
import org.incode.eurocommercial.contactapp.fixture.scenarios.demo.DemoFixture;
import org.incode.eurocommercial.contactapp.integtests.tests.ContactAppIntegTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ContactRoleIntegTest extends ContactAppIntegTest {

    @Inject
    FixtureScripts fixtureScripts;

    @Inject
    ContactGroupRepository contactGroupRepository;

    @Inject
    ContactRepository contactRepository;

    @Inject
    ContactRoleRepository contactRoleRepository;

    DemoFixture fs;

    ContactRole contactRole;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        // given
        fs = new DemoFixture();
        fixtureScripts.runFixtureScript(fs, null);
        nextTransaction();

        this.contactRole = fs.getContacts().get(0).getContactRoles().first();
        assertThat(this.contactRole).isNotNull();
    }

    public static class AlsoInGroup extends ContactRoleIntegTest {

        @Test
        public void happy_case_using_existing_role_name() throws Exception {
            // given
            final int amountOfContactRolesBefore = contactRoleRepository.listAll().size();
            assertThat(amountOfContactRolesBefore).isNotZero();

            // when
            final ContactGroup contactGroup = fakeDataService.collections().anyOf(this.contactRole.choices0AlsoInGroup());
            final String existingRole = fakeDataService.collections().anyOf(this.contactRole.choices1AlsoInGroup());
            final ContactRole newRole = wrap(this.contactRole).alsoInGroup(contactGroup, existingRole, null);

            // then
            assertThat(contactRoleRepository.listAll().size()).isEqualTo(amountOfContactRolesBefore + 1);
            assertThat(contactRoleRepository.listAll()).contains(newRole);
        }

        @Test
        public void happy_case_using_new_role_name() throws Exception {
            // given
            final int amountOfContactRolesBefore = contactRoleRepository.listAll().size();
            assertThat(amountOfContactRolesBefore).isNotZero();

            // when
            final ContactGroup contactGroup = fakeDataService.collections().anyOf(this.contactRole.choices0AlsoInGroup());
            final String newRoleName = "New role";
            final ContactRole newRole = wrap(this.contactRole).alsoInGroup(contactGroup, null, newRoleName);

            // then
            assertThat(contactRoleRepository.listAll().size()).isEqualTo(amountOfContactRolesBefore + 1);
            assertThat(contactRoleRepository.listAll()).contains(newRole);
        }

        @Test
        public void happy_case_using_new_role_name_which_also_in_list() throws Exception {
            // given
            final int amountOfContactRolesBefore = contactRoleRepository.listAll().size();
            assertThat(amountOfContactRolesBefore).isNotZero();

            // when
            final ContactGroup contactGroup = fakeDataService.collections().anyOf(this.contactRole.choices0AlsoInGroup());
            final String existingNewRole = fakeDataService.collections().anyOf(this.contactRole.choices1AlsoInGroup());
            final ContactRole newRole = wrap(this.contactRole).alsoInGroup(contactGroup, null, existingNewRole);

            // then
            assertThat(contactRoleRepository.listAll().size()).isEqualTo(amountOfContactRolesBefore + 1);
            assertThat(contactRoleRepository.listAll()).contains(newRole);
        }

        @Test
        public void when_no_contact_group_specified() throws Exception {
            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: 'Contact Group' is mandatory");

            // when
            final ContactGroup contactGroup = null;
            final String existingRole = fakeDataService.collections().anyOf(this.contactRole.choices1AlsoInGroup());
            wrap(this.contactRole).alsoInGroup(contactGroup, existingRole, null);
        }

        @Test
        public void when_no_role_specified() throws Exception {
            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Must specify either an (existing) role or a new role");

            // when
            final ContactGroup contactGroup = fakeDataService.collections().anyOf(this.contactRole.choices0AlsoInGroup());
            wrap(this.contactRole).alsoInGroup(contactGroup, null, null);
        }

        @Test
        public void when_both_existing_role_and_new_role_specified() throws Exception {
            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Must specify either an (existing) role or a new role");

            // when
            final ContactGroup contactGroup = fakeDataService.collections().anyOf(this.contactRole.choices0AlsoInGroup());
            final String existingRole = fakeDataService.collections().anyOf(this.contactRole.choices1AlsoInGroup());
            wrap(this.contactRole).alsoInGroup(contactGroup, existingRole, "New role");
        }

        @Test
        public void possible_groups_should_not_include_any_for_which_contact_already_has_a_role() throws Exception {
            // given
            final List<ContactGroup> groupsContactHasRoleFor =
                    contactRole.getContact().getContactRoles()
                            .stream()
                            .map(ContactRole::getContactGroup)
                            .collect(Collectors.toList());

            // when
            final List<ContactGroup> possibleGroups = contactRole.choices0AlsoInGroup();

            // then
            for (ContactGroup contactGroup : possibleGroups) {
                assertThat(groupsContactHasRoleFor).doesNotContain(contactGroup);
            }
        }

    }

    public static class AlsoWithContact extends ContactRoleIntegTest {

        @Test
        public void happy_case_using_existing_role_name() throws Exception {
            // given
            final int amountOfContactRolesBefore = contactRoleRepository.listAll().size();
            assertThat(amountOfContactRolesBefore).isNotZero();

            // when
            final Contact contact = fakeDataService.collections().anyOf(this.contactRole.choices0AlsoWithContact());
            final String existingRole = fakeDataService.collections().anyOf(this.contactRole.choices1AlsoWithContact());
            final ContactRole newRole = wrap(this.contactRole).alsoWithContact(contact, existingRole, null);

            // then
            assertThat(contactRoleRepository.listAll().size()).isEqualTo(amountOfContactRolesBefore + 1);
            assertThat(contactRoleRepository.listAll()).contains(newRole);
        }

        @Test
        public void happy_case_using_new_role_name() throws Exception {
            // given
            final int amountOfContactRolesBefore = contactRoleRepository.listAll().size();
            assertThat(amountOfContactRolesBefore).isNotZero();

            // when
            final Contact contact = fakeDataService.collections().anyOf(this.contactRole.choices0AlsoWithContact());
            final String newRoleName = "New role";
            final ContactRole newRole = wrap(this.contactRole).alsoWithContact(contact, null, newRoleName);

            // then
            assertThat(contactRoleRepository.listAll().size()).isEqualTo(amountOfContactRolesBefore + 1);
            assertThat(contactRoleRepository.listAll()).contains(newRole);
        }

        @Test
        public void happy_case_using_new_role_name_which_also_in_list() throws Exception {
            // given
            final int amountOfContactRolesBefore = contactRoleRepository.listAll().size();
            assertThat(amountOfContactRolesBefore).isNotZero();

            // when
            final Contact contact = fakeDataService.collections().anyOf(this.contactRole.choices0AlsoWithContact());
            final String newExistingRoleName = fakeDataService.collections().anyOf(this.contactRole.choices1AlsoWithContact());
            final ContactRole newRole = wrap(this.contactRole).alsoWithContact(contact, null, newExistingRoleName);

            // then
            assertThat(contactRoleRepository.listAll().size()).isEqualTo(amountOfContactRolesBefore + 1);
            assertThat(contactRoleRepository.listAll()).contains(newRole);
        }

        @Test
        public void when_no_contact_specified() throws Exception {
            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: 'Contact' is mandatory");

            // when
            final Contact contact = null;
            final String existingRole = fakeDataService.collections().anyOf(this.contactRole.choices1AlsoWithContact());
            wrap(this.contactRole).alsoWithContact(contact, existingRole, null);
        }

        @Test
        public void when_no_role_specified() throws Exception {
            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Must specify either an (existing) role or a new role");

            // when
            final Contact contact = fakeDataService.collections().anyOf(this.contactRole.choices0AlsoWithContact());
            wrap(this.contactRole).alsoWithContact(contact, null, null);
        }

        @Test
        public void when_both_existing_role_and_new_role_specified() throws Exception {
            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Must specify either an (existing) role or a new role");

            // when
            final Contact contact = fakeDataService.collections().anyOf(this.contactRole.choices0AlsoWithContact());
            final String existingRole = fakeDataService.collections().anyOf(this.contactRole.choices1AlsoWithContact());
            wrap(this.contactRole).alsoWithContact(contact, existingRole, "New role");
        }

        @Test
        public void possible_contacts_should_not_include_any_which_already_have_a_role_in_group() throws Exception {
            // given
            final List<Contact> contactsWhichAlreadyHaveRole =
                    contactRole.getContactGroup().getContactRoles()
                            .stream()
                            .map(ContactRole::getContact)
                            .collect(Collectors.toList());

            // when
            final List<Contact> possibleContacts = contactRole.choices0AlsoWithContact();

            // then
            for (Contact contact : possibleContacts) {
                assertThat(contactsWhichAlreadyHaveRole).doesNotContain(contact);
            }
        }

    }

    public static class Edit extends ContactRoleIntegTest {

        @Test
        public void happy_case_using_existing_role_name() throws Exception {
            // when
            final String existingRoleName = fakeDataService.collections().anyOf(this.contactRole.choices0Edit());
            wrap(this.contactRole).edit(existingRoleName, null);

            // then
            assertThat(this.contactRole.getRoleName()).isEqualTo(existingRoleName);
        }

        @Test
        public void happy_case_using_new_role_name() throws Exception {
            // given
            final String newRoleName = "New role";
            List<String> rolesBefore = contactRoleRepository.listAll()
                    .stream()
                    .map(ContactRole::getRoleName)
                    .collect(Collectors.toList());
            assertThat(rolesBefore).doesNotContain(newRoleName);

            // when
            wrap(this.contactRole).edit(null, newRoleName);

            // then
            assertThat(this.contactRole.getRoleName()).isEqualTo(newRoleName);
            List<String> rolesAfter = contactRoleRepository.listAll()
                    .stream()
                    .map(ContactRole::getRoleName)
                    .collect(Collectors.toList());
            assertThat(rolesAfter).contains(newRoleName);
        }

        @Test
        public void happy_case_using_new_role_name_which_also_in_list() throws Exception {
            // when
            final String existingRoleNameAsNewRoleName = fakeDataService.collections().anyOf(this.contactRole.choices0Edit());
            wrap(this.contactRole).edit(null, existingRoleNameAsNewRoleName);

            // then
            assertThat(this.contactRole.getRoleName()).isEqualTo(existingRoleNameAsNewRoleName);
        }

        @Test
        public void when_no_role_specified() throws Exception {
            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Must specify either an (existing) role or a new role");

            // when
            wrap(this.contactRole).edit(null, null);
        }

        @Test
        public void when_both_existing_role_and_new_role_specified() throws Exception {
            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Must specify either an (existing) role or a new role");

            // when
            final String newRoleName = fakeDataService.collections().anyOf(this.contactRole.choices0Edit());
            wrap(this.contactRole).edit(newRoleName, "New role");
        }

    }

}