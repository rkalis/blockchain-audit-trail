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
package org.incode.eurocommercial.contactapp.app.services.tenancy;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.userprof.UserProfileService;

import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.user.ApplicationUser;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class UserProfileServiceWithGreeting implements UserProfileService {

    @Override
    @Programmatic
    public String userProfileName() {
        final ApplicationUser applicationUser = meService.me();

        final StringBuilder buf = new StringBuilder();
        final String username = applicationUser.getName();

        buf.append("Hi ");
        buf.append(username);

        return buf.toString();
    }


    @Inject
    private MeService meService;
}
