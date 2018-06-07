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
package org.incode.eurocommercial.contactapp.integtests.tests.number;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.incode.eurocommercial.contactapp.dom.contactable.ContactableEntity;
import org.incode.eurocommercial.contactapp.dom.number.ContactNumber;
import org.incode.eurocommercial.contactapp.dom.number.ContactNumberRepository;
import org.incode.eurocommercial.contactapp.dom.number.ContactNumberType;
import org.incode.eurocommercial.contactapp.fixture.scenarios.demo.DemoFixture;
import org.incode.eurocommercial.contactapp.integtests.tests.ContactAppIntegTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ContactNumberIntegTest extends ContactAppIntegTest {

    @Inject
    FixtureScripts fixtureScripts;

    @Inject
    ContactNumberRepository contactNumberRepository;

    ContactNumber contactNumber;

    DemoFixture fs;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        // given
        fs = new DemoFixture();
        fixtureScripts.runFixtureScript(fs, null);

        contactNumber = fs.getContacts().get(0).getContactNumbers().first();
        nextTransaction();

        assertThat(contactNumber).isNotNull();
    }

    public static class Create extends ContactNumberIntegTest {

        @Test
        public void add_number_with_existing_type() throws Exception {
            // given
            final String contactNumberType = ContactNumberType.OFFICE.title();
            final String newOfficePhoneNumber = randomPhoneNumber();
            assertThat(contactNumberRepository.listAll())
                    .extracting(ContactNumber::getNumber)
                    .doesNotContain(newOfficePhoneNumber);

            // when
            wrap(this.contactNumber).create(newOfficePhoneNumber, contactNumberType, null);
            nextTransaction();

            // then
            assertThat(contactNumberRepository.listAll())
                    .extracting(ContactNumber::getNumber)
                    .contains(newOfficePhoneNumber);
        }

        @Test
        public void add_number_with_new_type() throws Exception {
            // given
            final String contactNumberType = "New type";
            final String newOfficePhoneNumber = randomPhoneNumber();
            assertThat(contactNumberRepository.listAll())
                    .extracting(ContactNumber::getNumber)
                    .doesNotContain(newOfficePhoneNumber);

            // when
            wrap(this.contactNumber).create(newOfficePhoneNumber, null, contactNumberType);
            nextTransaction();

            // then
            assertThat(contactNumberRepository.listAll())
                    .extracting(ContactNumber::getNumber)
                    .contains(newOfficePhoneNumber);
        }

        @Test
        public void add_number_when_already_have_number_of_any_type() throws Exception {
            // given
            final String existingNumber = this.contactNumber.getNumber();
            final String newType = "New type";
            assertThat(this.contactNumber.getType()).isNotEqualToIgnoringCase(newType);

            // when
            wrap(this.contactNumber).create(existingNumber, null, newType);

            // then
            assertThat(this.contactNumber.getType()).isEqualTo(newType);
            assertThat(this.contactNumber.getNumber()).isEqualTo(existingNumber);
        }

        @Test
        public void when_no_type_specified() throws Exception {
            // given
            final String newPhoneNumber = randomPhoneNumber();

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: Must specify either an (existing) type or a new type");

            // when
            wrap(this.contactNumber).create(newPhoneNumber, null, null);
        }

        @Test
        public void when_both_existing_type_and_new_type_specified() throws Exception {
            // given
            final String newPhoneNumber = randomPhoneNumber();

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: Must specify either an (existing) type or a new type");

            // when
            wrap(this.contactNumber).create(newPhoneNumber, ContactNumberType.OFFICE.title(), "New type");
        }

        @Test
        public void when_no_number_provided() throws Exception {
            // given
            final String noNumber = null;

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: 'Number' is mandatory");

            // when
            wrap(this.contactNumber).create(noNumber, ContactNumberType.OFFICE.title(), null);
        }

        @Test
        public void invalid_number_format() throws Exception {
            // given
            final String invalidNumber = "This is an invalid number";

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: Phone number should be in form: +44 1234 5678");

            // when
            wrap(this.contactNumber).create(invalidNumber, ContactNumberType.OFFICE.title(), null);
        }

    }

    public static class Edit extends ContactNumberIntegTest {

        @Test
        public void change_number() throws Exception {
            // given
            final String oldNumber = contactNumber.getNumber();
            final String newNumber = randomPhoneNumber();

            // when
            wrap(this.contactNumber).edit(newNumber, this.contactNumber.default1Edit(), null);

            // then
            assertThat(contactNumber.getNumber()).isEqualTo(newNumber);
            assertThat(contactNumberRepository.listAll())
                    .extracting(ContactNumber::getNumber)
                    .doesNotContain(oldNumber);
        }

        @Test
        public void change_type_to_existing() throws Exception {
            // given
            final String currentType = this.contactNumber.getType();
            final String existingType = ContactNumberType.OFFICE.title();
            assertThat(currentType).isNotEqualToIgnoringCase(existingType);
            final long amountOfCurrentType = contactNumberRepository.listAll()
                    .stream()
                    .filter(conNum -> conNum.getType().equalsIgnoreCase(currentType))
                    .count();
            final long amountOfNewType = contactNumberRepository.listAll()
                    .stream()
                    .filter(conNum -> conNum.getType().equalsIgnoreCase(existingType))
                    .count();

            // when
            wrap(this.contactNumber).edit(this.contactNumber.default0Edit(), existingType, null);

            // then
            assertThat(this.contactNumber.getType()).isEqualToIgnoringCase(existingType);
            assertThat(contactNumberRepository.listAll()
                    .stream()
                    .filter(conNum -> conNum.getType().equalsIgnoreCase(currentType))
                    .count()).isEqualTo(amountOfCurrentType - 1);
            assertThat(contactNumberRepository.listAll()
                    .stream()
                    .filter(conNum -> conNum.getType().equalsIgnoreCase(existingType))
                    .count()).isEqualTo(amountOfNewType + 1);
        }

        @Test
        public void change_type_to_new_type() throws Exception {
            // given
            final String currentType = this.contactNumber.getType();
            final String newType = "New Type";
            assertThat(currentType).isNotEqualToIgnoringCase(newType);
            final long amountOfCurrentType = contactNumberRepository.listAll()
                    .stream()
                    .filter(conNum -> conNum.getType().equalsIgnoreCase(currentType))
                    .count();
            final long amountOfNewType = contactNumberRepository.listAll()
                    .stream()
                    .filter(conNum -> conNum.getType().equalsIgnoreCase(newType))
                    .count();

            // when
            wrap(this.contactNumber).edit(this.contactNumber.default0Edit(), null, newType);

            // then
            assertThat(this.contactNumber.getType()).isEqualToIgnoringCase(newType);
            assertThat(contactNumberRepository.listAll()
                    .stream()
                    .filter(conNum -> conNum.getType().equalsIgnoreCase(currentType))
                    .count()).isEqualTo(amountOfCurrentType - 1);
            assertThat(contactNumberRepository.listAll()
                    .stream()
                    .filter(conNum -> conNum.getType().equalsIgnoreCase(newType))
                    .count()).isEqualTo(amountOfNewType + 1);
        }

        @Test
        public void when_change_number_to_already_existing() throws Exception {
            // given
            final String existingNumber = fakeDataService.collections()
                    .anyOfExcept(
                            contactNumberRepository.listAll(),
                            (ContactNumber conNum) -> conNum.equals(this.contactNumber))
                    .getNumber();
            assertThat(existingNumber).isNotEqualToIgnoringCase(this.contactNumber.getNumber());

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("A contact number with this number already exists");

            // when
            wrap(this.contactNumber).edit(existingNumber, this.contactNumber.default1Edit(), null);
        }

        @Test
        public void when_no_type_specified() throws Exception {
            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: Must specify either an (existing) type or a new type");

            // when
            wrap(this.contactNumber).edit(this.contactNumber.default0Edit(), null, null);
        }

        @Test
        public void when_both_existing_type_and_new_type_specified() throws Exception {
            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: Must specify either an (existing) type or a new type");

            // when
            wrap(this.contactNumber).edit(this.contactNumber.default0Edit(), this.contactNumber.default1Edit(), "New type");
        }

        @Test
        public void when_no_number_provided() throws Exception {
            // given
            final String noNumber = null;

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: 'Number' is mandatory");

            // when
            wrap(this.contactNumber).edit(noNumber, this.contactNumber.default1Edit(), null);
        }

        @Test
        public void invalid_number_format() throws Exception {
            // given
            final String invalidNumber = "This is an invalid number";

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("Reason: Phone number should be in form: +44 1234 5678");

            // when
            wrap(this.contactNumber).edit(invalidNumber, this.contactNumber.default1Edit(), null);
        }

        @Test
        public void without_new_number() throws Exception {
            // given
            assertThat(this.contactNumber.getNumber()).isEqualTo("+44 1233 444 555");

            // when
            wrap(this.contactNumber).edit(this.contactNumber.default0Edit(), null, "New Type");

            // then
            assertThat(this.contactNumber.getType()).isEqualTo("New Type");
            // Naturally the number hasn't changed; this assertion merely ensures validateEdit
            // does not throw an exception
            assertThat(this.contactNumber.getNumber()).isEqualTo("+44 1233 444 555");
        }
    }

    public static class Delete extends ContactNumberIntegTest {

        @Test
        public void happy_case() throws Exception {
            // given
            final String numberToBeDeleted = this.contactNumber.getNumber();

            final int contactNumbersBefore = contactNumberRepository.listAll().size();
            final ContactableEntity owner = this.contactNumber.getOwner();
            assertThat(contactNumbersBefore).isNotZero();
            assertThat(owner.getContactNumbers().first()).isEqualTo(this.contactNumber);
            assertThat(owner.getContactNumbers()).contains(this.contactNumber);

            // when
            wrap(this.contactNumber).delete();

            // then
            assertThat(contactNumberRepository.findByNumber(numberToBeDeleted)).isNull();
            assertThat(owner.getContactNumbers()).extracting(ContactNumber::getNumber).doesNotContain(numberToBeDeleted);
        }

    }

}