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
package org.incode.eurocommercial.contactapp.dom.contact;

import org.junit.Before;
import org.junit.Test;

import org.incode.eurocommercial.contactapp.dom.contacts.Contact;
import static org.assertj.core.api.Assertions.assertThat;

public class ContactTest {

    Contact contact;

    @Before
    public void setUp() throws Exception {
        contact = new Contact();
    }

    public static class Name extends ContactTest {

        @Test
        public void happyCase() throws Exception {
            // given
            String name = "Foobar";
            assertThat(contact.getName()).isNull();

            // when
            contact.setName(name);

            // then
            assertThat(contact.getName()).isEqualTo(name);
        }
    }
    public static class Change extends ContactTest {

        @Test
        public void happyCase() throws Exception {
            // given
            String name = "New name";
            String company = "New company";
            String email = "New email";
            String notes = "New content";

            // when
            contact.edit(name, company, email, notes);
            // then
            assertThat(contact.getName()).isEqualTo(name);
            assertThat(contact.getEmail()).isEqualTo(email);
            assertThat(contact.getCompany()).isEqualTo(company);
            assertThat(contact.getNotes()).isEqualTo(notes);
        }

    }
}
