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
package org.incode.eurocommercial.contactapp.dom.contacts;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ContactMenuTest {

    ContactMenu contactMenu;

    public static final String query = "a";
    public static final String queryInCaseInsensitiveRegex = "(?i).*a.*";

    public static final String regexInQuery = "*a*";
    public static final String regexInQueryInCaseInsensitiveRegex = "(?i).*a.*";

    public static final String regexInQuery2 = "?a?";
    public static final String regexInQueryInCaseInsensitiveRegex2 = "(?i).a.";

    @Before
    public void setup() {
        contactMenu = new ContactMenu();
    }

    @Test
    public void toCaseInsensitiveRegexTests() throws Exception {
        assertEquals(contactMenu.toCaseInsensitiveRegex(query), queryInCaseInsensitiveRegex);
        assertEquals(contactMenu.toCaseInsensitiveRegex(regexInQuery), regexInQueryInCaseInsensitiveRegex);
        assertEquals(contactMenu.toCaseInsensitiveRegex(regexInQuery2), regexInQueryInCaseInsensitiveRegex2);
    }
}
