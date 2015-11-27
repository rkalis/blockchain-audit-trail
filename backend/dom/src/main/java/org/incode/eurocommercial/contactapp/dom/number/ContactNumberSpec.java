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

import java.util.regex.Pattern;

import org.apache.isis.applib.spec.Specification;

public class ContactNumberSpec implements Specification {

    static final String ERROR_MESSAGE = "Phone number should be in form: +44 1234 5678";
    private static Pattern pattern = Pattern.compile("\\+[\\d]{2} [\\d ]+\\d");

    @Override
    public String satisfies(final Object obj) {
        if(obj == null || !(obj instanceof String)) {
            return null;
        }
        String str = (String) obj;
        if(pattern.matcher(str).matches()) {
            return null;
        }
        return ERROR_MESSAGE;
    }
}
