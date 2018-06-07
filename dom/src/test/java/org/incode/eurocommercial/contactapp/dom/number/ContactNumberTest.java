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
package org.incode.eurocommercial.contactapp.dom.number;

import org.junit.Before;
import org.junit.Test;

import org.isisaddons.module.fakedata.dom.FakeDataService;

import static org.assertj.core.api.Assertions.assertThat;

public class ContactNumberTest {

    ContactNumber contactNumber;
    FakeDataService fakeDataService = new FakeDataService() {{
        init();
    }};

    @Before
    public void setUp() throws Exception {
        contactNumber = new ContactNumber();
    }

    public static class Change extends ContactNumberTest {

        @Test
        public void happy_case_when_specify_as_existing_type() throws Exception {
            // given
            String existingType = fakeDataService.enums().anyOf(ContactNumberType.class).title();
            String number = randomPhoneNumber();

            // when
            contactNumber.edit(number, existingType, null);

            // then
            assertThat(contactNumber.getType()).isEqualTo(existingType);
            assertThat(contactNumber.getNumber()).isEqualTo(number);
        }

        @Test
        public void happy_case_when_specify_as_new_type() throws Exception {
            // given
            String type = fakeDataService.strings().upper(ContactNumber.MaxLength.TYPE);
            String number = randomPhoneNumber();

            // when
            contactNumber.edit(number, null, type);

            // then
            assertThat(contactNumber.getType()).isEqualTo(type);
            assertThat(contactNumber.getNumber()).isEqualTo(number);
        }

        private String randomPhoneNumber() {
            return "+" + fakeDataService.strings().digits(2) + " " + fakeDataService.strings().digits(4) + " " + fakeDataService.strings().digits(6);
        }

    }

}
