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
package org.incode.eurocommercial.contactapp.app.rest.v1.contacts;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ContactableViewModelTest {

    ContactableViewModel contact1;
    ContactableViewModel contact2;
    ContactableViewModel contact3;
    ContactableViewModel contactGroup;

    @Before
    public void setUp() throws Exception {
        contact1 = new ContactableViewModel() {
            @Override public String getName() {
                return "Joe Smith";
            }

            @Override public Type getType() {
                return Type.CONTACT;
            }
        };
        contact2 = new ContactableViewModel() {
            @Override public String getName() {
                return "Madonna";
            }

            @Override public Type getType() {
                return Type.CONTACT;
            }
        };
        contact3 = new ContactableViewModel() {
            @Override public String getName() {
                return "Helena Bonham-Carter";
            }

            @Override public Type getType() {
                return Type.CONTACT;
            }
        };
        contactGroup = new ContactableViewModel() {
            @Override public String getName() {
                return "Oxford";
            }

            @Override public Type getType() {
                return Type.CONTACT_GROUP;
            }
        };
    }

    @Test
    public void testForContact() throws Exception {
        assertThat(ContactableViewModel.firstNameFrom(contact1.getName())).isEqualTo("Joe");
        assertThat(ContactableViewModel.lastNameFrom(contact1.getName())).isEqualTo("Smith");
    }

    @Test
    public void testForContactWithNoFirstName() throws Exception {
        assertThat(ContactableViewModel.firstNameFrom(contact2.getName())).isEqualTo("");
        assertThat(ContactableViewModel.lastNameFrom(contact2.getName())).isEqualTo("Madonna");
    }

    @Test
    public void testForContactWithDoubleBarrelledName() throws Exception {
        assertThat(ContactableViewModel.firstNameFrom(contact3.getName())).isEqualTo("Helena");
        assertThat(ContactableViewModel.lastNameFrom(contact3.getName())).isEqualTo("Bonham-Carter");
    }

    @Test
    public void forContactGroup() throws Exception {
        assertThat(ContactableViewModel.firstNameFrom(contactGroup.getName())).isEqualTo("");
        assertThat(ContactableViewModel.lastNameFrom(contactGroup.getName())).isEqualTo("Oxford");
    }

}