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
package org.incode.eurocommercial.contactapp.integtests.tests;

import java.util.Collection;
import java.util.Objects;

import javax.inject.Inject;

import org.junit.BeforeClass;

import org.apache.isis.core.integtestsupport.IntegrationTestAbstract2;

import org.isisaddons.module.fakedata.dom.FakeDataService;

import org.incode.eurocommercial.contactapp.app.ContactAppAppManifest;
import org.incode.eurocommercial.contactapp.dom.number.ContactNumber;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class ContactAppIntegTest extends IntegrationTestAbstract2 {

    @Inject
    protected FakeDataService fakeDataService;

    @BeforeClass
    public static void initClass() {
        bootstrapUsing(ContactAppAppManifest.BUILDER.withAdditionalServices(FakeDataService.class));
    }

    protected static void assertContains(
            final Collection<ContactNumber> contactNumbers,
            final String type,
            final String number) {
        assertThat(contactNumbers.stream().filter(x -> Objects.equals(x.getType(), type)))
                .extracting(ContactNumber::getNumber)
                .contains(number);
    }

    protected static void assertNotContains(
            final Collection<ContactNumber> contactNumbers,
            final String number) {
        assertThat(contactNumbers)
                .extracting(ContactNumber::getNumber)
                .doesNotContain(number);
    }

    protected String randomPhoneNumber() {
        return "+" + fakeDataService.strings().digits(2) + " " + fakeDataService.strings().digits(4) + " " + fakeDataService.strings().digits(6);
    }

}
