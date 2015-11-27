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

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.xactn.TransactionService;

import org.incode.eurocommercial.contactapp.dom.contacts.Contact;
import org.incode.eurocommercial.contactapp.dom.contacts.ContactRepository;
import org.incode.eurocommercial.contactapp.dom.group.ContactGroup;
import org.incode.eurocommercial.contactapp.dom.group.ContactGroupRepository;
import org.incode.eurocommercial.contactapp.dom.role.ContactRole;
import org.incode.eurocommercial.contactapp.dom.role.ContactRoleRepository;
import org.incode.eurocommercial.contactapp.fixture.scenarios.demo.DemoFixture;
import org.incode.eurocommercial.contactapp.integtests.tests.ContactAppIntegTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ContactRepositoryTest extends ContactAppIntegTest {

    @Inject
    FixtureScripts fixtureScripts;

    @Inject
    ContactRepository contactRepository;

    @Inject
    ContactGroupRepository contactGroupRepository;

    @Inject
    TransactionService transactionService;

    @Before
    public void setUp() throws Exception {
        // given
        FixtureScript fs = new DemoFixture();
        fixtureScripts.runFixtureScript(fs, null);
        transactionService.nextTransaction();
    }

    public static class ListAll extends ContactRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given, when
            final List<Contact> contacts = contactRepository.listAll();

            // then
            assertThat(contacts.size()).isEqualTo(13);
        }
    }

    public static class ListOrphanedContacts extends ContactRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given
            assertThat(contactRepository.listOrphanedContacts()).isEmpty();

            // when
            final ContactGroup contactGroup = contactGroupRepository.listAll().get(0);
            wrap(contactGroup).delete(true);

            // then
            assertThat(contactRepository.listOrphanedContacts().size()).isEqualTo(5);
        }
    }

    public static class Find extends ContactRepositoryTest {

        @Test
        public void multipleFindersSingleQuery() throws Exception {
            // given
            String query = "(?i).*a.*";
            // when
            final List<Contact> contacts = contactRepository.find(query);

            // then
            assertThat(contacts.size()).isEqualTo(13);
        }
    }

    public static class FindByName extends ContactRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            // given
            final String contactName = contactRepository.listAll().get(0).getName();

            // when
            final List<Contact> contacts = contactRepository.findByName(contactName);

            // then
            assertThat(contacts.size()).isGreaterThan(0);
        }

        @Test
        public void partial_name() throws Exception {
            // given
            final String contactName = contactRepository.listAll().get(0).getName();
            final String firstName = contactName.split(" ")[0];
            final String firstLetter = "b";

            // when
            final List<Contact> contact = contactRepository.findByName("(?i).*" + firstName + ".*");
            final List<Contact> contacts = contactRepository.findByName("(?i).*" + firstLetter + ".*");

            // then
            assertThat(contact.size()).isEqualTo(1);
            assertThat(contacts.size()).isEqualTo(5);
        }

        @Test
        public void no_matches() throws Exception {
            // given
            final String contactName = "Not a name";

            // when
            final List<Contact> contacts = contactRepository.findByName("(?i).*" + contactName + ".*");

            // then
            assertThat(contacts.size()).isEqualTo(0);
        }
    }

    public static class FindByCompany extends ContactRepositoryTest {

        @Test
        public void happy_case() throws Exception {
            // given
            final String contactCompany = contactRepository.listAll().get(0).getCompany();

            // when
            final List<Contact> contacts = contactRepository.findByCompany(contactCompany);

            // then
            assertThat(contacts.size()).isGreaterThan(0);
        }

        @Test
        public void partial_name() throws Exception {
            // given
            final String contactCompany = contactRepository.listAll().get(0).getCompany();
            final String substring = contactCompany.substring(1, 3);

            // when
            final List<Contact> contact = contactRepository.findByCompany("(?i).*" + substring + ".*");

            // then
            assertThat(contact.size()).isGreaterThan(0);
        }

        @Test
        public void sadCase() throws Exception {
            // given
            final String contactCompany = "Not a name";

            // when
            final List<Contact> contacts = contactRepository.findByName("(?i).*" + contactCompany + ".*");

            // then
            assertThat(contacts.size()).isEqualTo(0);
        }
    }

    public static class FindByContactGroup extends ContactRepositoryTest {

        @Inject
        ContactGroupRepository contactGroupRepository;

        @Test
        public void happy_case() throws Exception {
            // given
            final ContactGroup contactGroup = contactGroupRepository.listAll().get(0);

            // when
            final List<Contact> contacts = contactRepository.findByContactGroup(contactGroup);

            // then
            assertThat(contacts.size()).isGreaterThan(0);
        }

        @Test
        public void no_matches() throws Exception {
            // given
            final ContactGroup contactGroup = new ContactGroup();

            // when
            final List<Contact> contacts = contactRepository.findByContactGroup(contactGroup);

            // then
            assertThat(contacts.size()).isEqualTo(0);
        }
    }

    public static class FindByContactRoleName extends ContactRepositoryTest {

        @Inject
        ContactRoleRepository contactRoleRepository;

        @Test
        public void happy_case() throws Exception {
            // given
            List<ContactRole> list = contactRoleRepository.listAll();
            String regex = "No ContactRoleName in fixtures";

            for(ContactRole contactRole : list) {
                if (contactRole.getRoleName() != null) {
                    regex = contactRole.getRoleName();
                }
            }

            // when
            final List<Contact> contacts = contactRepository.findByContactRoleName(regex);

            // then
            assertThat(contacts.size()).isGreaterThan(0);
        }

        @Test
        public void no_matches() throws Exception {
            // given
            final String roleName = "Not a role";

            // when
            final List<Contact> contacts = contactRepository.findByContactRoleName(roleName);

            // then
            assertThat(contacts.size()).isEqualTo(0);
        }
    }

}