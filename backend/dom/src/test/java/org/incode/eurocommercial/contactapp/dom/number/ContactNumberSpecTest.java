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

import static org.assertj.core.api.Assertions.assertThat;

public class ContactNumberSpecTest {

    private ContactNumberSpec spec;

    @Before
    public void setUp() throws Exception {
        spec = new ContactNumberSpec();
    }

    @Test
    public void happy_case() throws Exception {
        assertThat(spec.satisfies("+44 1234 5678")).isNull();
    }

    @Test
    public void happy_case_no_spaces() throws Exception {
        assertThat(spec.satisfies("+44 12345678")).isNull();
    }

    @Test
    public void sad_case_with_brackets() throws Exception {
        assertThat(spec.satisfies("+44 (0)1234 5678")).isEqualTo(ContactNumberSpec.ERROR_MESSAGE);
    }

    @Test
    public void sad_case_missing_first_plus() throws Exception {
        assertThat(spec.satisfies("44 1234 5678")).isEqualTo(ContactNumberSpec.ERROR_MESSAGE);
    }

    @Test
    public void sad_case_no_space() throws Exception {
        assertThat(spec.satisfies("+441234 5678")).isEqualTo(ContactNumberSpec.ERROR_MESSAGE);
    }

    @Test
    public void sad_case_ends_with_space() throws Exception {
        assertThat(spec.satisfies("+44 1234 5678 ")).isEqualTo(ContactNumberSpec.ERROR_MESSAGE);
    }

    @Test
    public void when_not_a_string() throws Exception {
        assertThat(spec.satisfies(new Object())).isNull();
    }

    @Test
    public void when_null() throws Exception {
        assertThat(spec.satisfies(new Object())).isNull();
    }
}