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
package org.incode.eurocommercial.contactapp.integtests.tests.homepage;

import java.util.List;
import java.util.SortedSet;

import javax.inject.Inject;

import com.google.common.base.Objects;
import com.google.common.collect.FluentIterable;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.isisaddons.module.fakedata.dom.FakeDataService;

import org.incode.eurocommercial.contactapp.app.services.homepage.HomePageService;
import org.incode.eurocommercial.contactapp.app.services.homepage.HomePageViewModel;
import org.incode.eurocommercial.contactapp.dom.contacts.ContactRepository;
import org.incode.eurocommercial.contactapp.dom.country.Country;
import org.incode.eurocommercial.contactapp.dom.country.CountryRepository;
import org.incode.eurocommercial.contactapp.dom.group.ContactGroup;
import org.incode.eurocommercial.contactapp.dom.role.ContactRole;
import org.incode.eurocommercial.contactapp.fixture.scenarios.demo.DemoFixture;
import org.incode.eurocommercial.contactapp.integtests.tests.ContactAppIntegTest;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class HomePageViewModelIntegTest extends ContactAppIntegTest {

    @Inject
    FixtureScripts fixtureScripts;

    @Inject
    ContactRepository contactRepository;
    @Inject
    CountryRepository countryRepository;

    @Inject
    FakeDataService fakeDataService;

    @Inject
    HomePageService homePageService;

    DemoFixture fs;

    HomePageViewModel homePageViewModel;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        // given
        fs = new DemoFixture();
        fixtureScripts.runFixtureScript(fs, null);
        nextTransaction();

        homePageViewModel = homePageService.homePage();
    }

    public static class ContactGroups extends HomePageViewModelIntegTest {

        @Test
        public void accessible() throws Exception {
            // when
            final List<ContactGroup> groups = homePageViewModel.getGroups();

            // then
            assertThat(groups.size()).isEqualTo(4);
        }

    }

    public static class CreateGroup extends HomePageViewModelIntegTest {

        @Test
        public void happy_case() throws Exception {

            // given
            final List<ContactGroup> groups = homePageViewModel.getGroups();
            final int sizeBefore = groups.size();

            // when
            final List<Country> list = countryRepository.listAll();
            final Country someCountry = fakeDataService.collections().anyOf(list);
            final String groupName = fakeDataService.strings().fixed(8);

            wrap(homePageViewModel).newContactGroup(someCountry, groupName);
            nextTransaction();

            // then
            final List<ContactGroup> groupsAfter = homePageViewModel.getGroups();
            assertThat(groupsAfter.size()).isEqualTo(sizeBefore + 1);
            assertThat(
                    FluentIterable.from(groupsAfter)
                            .filter(contactGroupOf(someCountry, groupName))
                            .toList())
                    .hasSize(1);
        }

        @Test
        public void when_no_country_specified() throws Exception {
            // given
            final Country noCountry = null;
            final String groupName = fakeDataService.strings().fixed(8);

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: 'Country' is mandatory");

            // when
            wrap(homePageViewModel).newContactGroup(noCountry, groupName);
        }

        @Test
        public void when_no_name_provided() throws Exception {
            // given
            final List<Country> list = countryRepository.listAll();
            final Country someCountry = fakeDataService.collections().anyOf(list);
            final String noName = null;

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: 'Name' is mandatory");

            // when
            wrap(homePageViewModel).newContactGroup(someCountry, noName);
        }

    }

    public static class DeleteGroup extends HomePageViewModelIntegTest {

        @Test
        public void happy_case() throws Exception {

            // given
            final List<Country> list = countryRepository.listAll();
            final Country someCountry = fakeDataService.collections().anyOf(list);
            final String groupName = fakeDataService.strings().fixed(8);

            wrap(homePageViewModel).newContactGroup(someCountry, groupName);

            final List<ContactGroup> groups = homePageViewModel.getGroups();

            final ContactGroup addedContactGroup = FluentIterable.from(groups)
                    .filter(contactGroupOf(someCountry, groupName))
                    .toList().get(0);

            final int sizeBefore = groups.size();

            final SortedSet<ContactRole> contactRoles = addedContactGroup.getContactRoles();
            assertThat(contactRoles).isEmpty();
            nextTransaction();

            // when
            final List<ContactGroup> contactGroupChoices = homePageViewModel.choices0DeleteContactGroup();
            nextTransaction();

            // then
            assertThat(contactGroupChoices).contains(addedContactGroup);

            // and when
            wrap(homePageViewModel).deleteContactGroup(addedContactGroup, true);
            nextTransaction();

            // then
            final List<ContactGroup> groupsAfter = homePageViewModel.getGroups();
            assertThat(groupsAfter).hasSize(sizeBefore - 1);

            assertThat(
                    FluentIterable
                            .from(groupsAfter)
                            .filter(contactGroupOf(someCountry, groupName))
                            .toList())
                    .isEmpty();
        }

    }

    private static com.google.common.base.Predicate<ContactGroup> contactGroupOf(
            final Country someCountry,
            final String groupName) {
        return contactGroup -> matches(someCountry, groupName, contactGroup);
    }

    private static boolean matches(final Country someCountry, final String groupName, final ContactGroup contactGroup) {
        return Objects.equal(contactGroup.getName(), groupName) &&
                Objects.equal(contactGroup.getCountry().getName(), someCountry.getName());
    }

}