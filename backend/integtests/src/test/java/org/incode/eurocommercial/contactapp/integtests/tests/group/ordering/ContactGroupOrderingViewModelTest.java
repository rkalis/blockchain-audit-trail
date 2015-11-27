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
package org.incode.eurocommercial.contactapp.integtests.tests.group.ordering;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;

import org.incode.eurocommercial.contactapp.dom.group.ContactGroup;
import org.incode.eurocommercial.contactapp.dom.group.ContactGroupRepository;
import org.incode.eurocommercial.contactapp.dom.group.ordering.ContactGroupOrderingViewModel;
import org.incode.eurocommercial.contactapp.dom.group.ordering.ContactGroup_fixDisplayOrder;
import org.incode.eurocommercial.contactapp.fixture.scenarios.demo.DemoFixture;
import org.incode.eurocommercial.contactapp.integtests.tests.ContactAppIntegTest;
import static org.assertj.core.api.Assertions.assertThat;

public class ContactGroupOrderingViewModelTest extends ContactAppIntegTest {

    @Inject
    FixtureScripts fixtureScripts;

    @Inject
    ContactGroupRepository contactGroupRepository;

    @Before
    public void setUp() throws Exception {
        // given
        FixtureScript fs = new DemoFixture();
        fixtureScripts.runFixtureScript(fs, null);
    }

    public static class Initialization extends ContactGroupOrderingViewModelTest {

        @Test
        public void happyCase() throws Exception {

            // given
            final List<ContactGroup> contactGroups = contactGroupRepository.listAll();
            final ContactGroup managementBoardGroup = contactGroups.get(1);
            assertThat(managementBoardGroup.getName()).isEqualTo("Management Board");

            // when
            final ContactGroupOrderingViewModel vm = wrap(mixin(ContactGroup_fixDisplayOrder.class, managementBoardGroup)).$$();

            // then
            assertThat(vm).isNotNull();
            assertThat(vm.getName()).isEqualTo("Management Board (Global)");

            final List<ContactGroupOrderingViewModel> contactGroupVms = vm.getContactGroups();

            assertThat(contactGroupVms.size()).isEqualTo(4);
            assertThat(contactGroupVms.get(0).getName()).isEqualTo("Amiens Property (France)");
            assertThat(contactGroupVms.get(1).getName()).isEqualTo("Management Board (Global)");
            assertThat(contactGroupVms.get(2).getName()).isEqualTo("Paris Office (France)");
            assertThat(contactGroupVms.get(3).getName()).isEqualTo("Regulatory (Global)");

            assertThat(contactGroupVms.get(1).getContactGroup()).isEqualTo(managementBoardGroup);
        }

    }

    public static class MoveUp extends ContactGroupOrderingViewModelTest {

        @Test
        public void happyCase() throws Exception {

            // given
            final ContactGroup managementBoardGroup = contactGroupRepository.listAll().get(1);
            final ContactGroup parisOfficeGroup = contactGroupRepository.listAll().get(2);
            assertThat(managementBoardGroup.getName()).isEqualTo("Management Board");

            final ContactGroupOrderingViewModel managementBoardVm =
                    wrap(mixin(ContactGroup_fixDisplayOrder.class, managementBoardGroup)).$$();

            // when
            managementBoardVm.moveUp();

            // then the underlying entities are reordered
            final List<ContactGroup> contactGroupsAfter = contactGroupRepository.listAll();

            assertThat(contactGroupsAfter.get(0).getName()).isEqualTo("Management Board");
            assertThat(contactGroupsAfter.get(0).getDisplayOrder()).isEqualTo(1);

            assertThat(contactGroupsAfter.get(1).getName()).isEqualTo("Amiens Property");
            assertThat(contactGroupsAfter.get(1).getDisplayOrder()).isNull();

            assertThat(contactGroupsAfter.get(2).getName()).isEqualTo("Paris Office");
            assertThat(contactGroupsAfter.get(2).getDisplayOrder()).isNull();

            assertThat(contactGroupsAfter.get(3).getName()).isEqualTo("Regulatory");
            assertThat(contactGroupsAfter.get(3).getDisplayOrder()).isNull();


            // and this is also reflected in the view model
            final List<ContactGroupOrderingViewModel> managementBoardVmContactGroups = managementBoardVm.getContactGroups();

            assertThat(managementBoardVmContactGroups.get(0).getName()).isEqualTo("Management Board (Global)");
            assertThat(managementBoardVmContactGroups.get(1).getName()).isEqualTo("Amiens Property (France)");
            assertThat(managementBoardVmContactGroups.get(2).getName()).isEqualTo("Paris Office (France)");
            assertThat(managementBoardVmContactGroups.get(3).getName()).isEqualTo("Regulatory (Global)");

            // and given
            final ContactGroupOrderingViewModel parisOfficeVm = wrap(managementBoardVmContactGroups.get(2));
            assertThat(parisOfficeVm.getName()).isEqualTo("Paris Office (France)");

            // when
            parisOfficeVm.moveUp();
            parisOfficeVm.moveUp();

            // then reordered once more
            final List<ContactGroup> contactGroupsAfter2 = contactGroupRepository.listAll();

            assertThat(contactGroupsAfter2.get(0).getName()).isEqualTo("Paris Office");
            assertThat(contactGroupsAfter2.get(0).getDisplayOrder()).isEqualTo(1);

            assertThat(contactGroupsAfter2.get(1).getName()).isEqualTo("Management Board");
            assertThat(contactGroupsAfter2.get(1).getDisplayOrder()).isEqualTo(2);

            assertThat(contactGroupsAfter2.get(2).getName()).isEqualTo("Amiens Property");
            assertThat(contactGroupsAfter2.get(2).getDisplayOrder()).isNull();

            assertThat(contactGroupsAfter2.get(3).getName()).isEqualTo("Regulatory");
            assertThat(contactGroupsAfter2.get(3).getDisplayOrder()).isNull();

        }

    }
}


