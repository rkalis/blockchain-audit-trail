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
package org.incode.eurocommercial.contactapp.dom.group;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ContactGroupTest {

    ContactGroup contactGroup;

    @Before
    public void setUp() throws Exception {
        contactGroup = new ContactGroup();
    }

    public static class Change extends ContactGroupTest {

        @Test
        public void happyCase() throws Exception {
            // given
            String name = "New name";
            String address = "New address";
            String email = "New email";
            String notes = "New content";

            // when
            contactGroup.edit(name, address, email, notes);
            // then
            assertThat(contactGroup.getName()).isEqualTo(name);
            assertThat(contactGroup.getEmail()).isEqualTo(email);
            assertThat(contactGroup.getAddress()).isEqualTo(address);
            assertThat(contactGroup.getNotes()).isEqualTo(notes);
        }

    }

}
