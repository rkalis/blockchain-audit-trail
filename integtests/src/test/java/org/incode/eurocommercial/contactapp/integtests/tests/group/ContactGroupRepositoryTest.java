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

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;

import org.incode.eurocommercial.contactapp.dom.group.ContactGroup;
import org.incode.eurocommercial.contactapp.dom.group.ContactGroupRepository;
import org.incode.eurocommercial.contactapp.fixture.scenarios.demo.DemoFixture;
import org.incode.eurocommercial.contactapp.integtests.tests.ContactAppIntegTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ContactGroupRepositoryTest extends ContactAppIntegTest {

    @Inject
    FixtureScripts fixtureScripts;

    @Inject
    ContactGroupRepository contactGroupRepository;

    @Before
    public void setUp() throws Exception {
        // given
        FixtureScript fs = new DemoFixture();
        fixtureScripts.runFixtureScript(fs, null);
        nextTransaction();
    }

    public static class FindByName extends ContactGroupRepositoryTest {

        @Test
        public void happy_case() throws Exception {
            // given
            final String searchStr = "a";

            // when
            final List<ContactGroup> contacts = contactGroupRepository.findByName("(?i).*" + searchStr + ".*");

            // then
            assertThat(contacts.size()).isEqualTo(4);
        }

    }
    public static class ListAll extends ContactGroupRepositoryTest {

        @Test
        public void happyCase() throws Exception {

            // when
            final List<ContactGroup> contactGroups = contactGroupRepository.listAll();

            // then
            assertThat(contactGroups.size()).isEqualTo(4);
            assertThat(contactGroups.get(0).getName()).isEqualTo("Amiens Property");
            assertThat(contactGroups.get(1).getName()).isEqualTo("Management Board");
            assertThat(contactGroups.get(2).getName()).isEqualTo("Paris Office");
            assertThat(contactGroups.get(3).getName()).isEqualTo("Regulatory");

        }

        @Test
        public void reorder() throws Exception {

            // given
            final List<ContactGroup> contactGroupsInitial = contactGroupRepository.listAll();
            contactGroupsInitial.get(2).setDisplayOrder(1); // move 'Paris Office' first
            contactGroupsInitial.get(1).setDisplayOrder(2); // move 'Management Board' second

            // when
            final List<ContactGroup> contactGroups = contactGroupRepository.listAll();

            // then
            assertThat(contactGroups.size()).isEqualTo(4);
            assertThat(contactGroups.get(0).getName()).isEqualTo("Paris Office");
            assertThat(contactGroups.get(1).getName()).isEqualTo("Management Board");
            assertThat(contactGroups.get(2).getName()).isEqualTo("Amiens Property");
            assertThat(contactGroups.get(3).getName()).isEqualTo("Regulatory");

        }

    }
    public static class Delete extends ContactGroupRepositoryTest {

        @Test
        public void delete() throws Exception {

            // given
            ContactGroup contactGroup = contactGroupRepository.listAll().get(0);
            assertThat(contactGroup.getContactRoles()).isNotEmpty();

            // when
            contactGroupRepository.delete(contactGroup);

            // then
            assertThat(contactGroup.getContactRoles()).isEmpty();

        }
    }
}


