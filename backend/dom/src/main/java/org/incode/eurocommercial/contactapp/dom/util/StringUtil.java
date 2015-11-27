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
package org.incode.eurocommercial.contactapp.dom.util;

import com.google.common.base.Strings;

public class StringUtil {
    private StringUtil() {
    }

    public static String firstNonEmpty(final String... str) {
        for (String s : str) {
            if (!Strings.isNullOrEmpty(s)) {
                return s;
            }
        }
        return null;
    }

    public static String eitherOr(final String role, final String newRole, final String thing) {
        if((Strings.isNullOrEmpty(role) && Strings.isNullOrEmpty(newRole)) || (!Strings.isNullOrEmpty(role) && !Strings.isNullOrEmpty(newRole))) {
            return "Must specify either an (existing) "
                    + thing
                    + " or a new "
                    + thing;
        }
        return null;
    }
}
