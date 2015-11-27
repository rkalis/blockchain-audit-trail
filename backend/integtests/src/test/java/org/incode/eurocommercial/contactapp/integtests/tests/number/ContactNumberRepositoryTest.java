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

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScripts;

import org.incode.eurocommercial.contactapp.dom.contacts.Contact;
import org.incode.eurocommercial.contactapp.dom.number.ContactNumber;
import org.incode.eurocommercial.contactapp.dom.number.ContactNumberRepository;
import org.incode.eurocommercial.contactapp.fixture.scenarios.demo.DemoFixture;
import org.incode.eurocommercial.contactapp.integtests.tests.ContactAppIntegTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ContactNumberRepositoryTest extends ContactAppIntegTest {

    @Inject
    FixtureScripts fixtureScripts;

    @Inject
    ContactNumberRepository contactNumberRepository;

    Contact contact;

    @Before
    public void setUp() throws Exception {
        // given
        DemoFixture fs = new DemoFixture();
        fixtureScripts.runFixtureScript(fs, null);
        contact = fs.getContacts().get(0);
    }

    public static class ListAll extends ContactNumberRepositoryTest {

        @Test
        public void happy_case() throws Exception {
            // given, when
            final List<ContactNumber> contactNumbers = contactNumberRepository.listAll();
            // then
            assertThat(contactNumbers.size()).isEqualTo(25);
        }

    }

    public static class FindByNumber extends ContactNumberRepositoryTest {

        @Test
        public void happy_case() throws Exception {
            // given
            final ContactNumber existingContactNumber = contact.getContactNumbers().first();
            assertThat(existingContactNumber).isNotNull();

            // when
            final ContactNumber contactNumber = contactNumberRepository.findByNumber(existingContactNumber.getNumber());

            // then
            assertThat(contactNumber).isEqualTo(existingContactNumber);
        }
    }
}