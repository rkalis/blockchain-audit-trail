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
package org.incode.eurocommercial.contactapp.app.seed;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;

import org.incode.eurocommercial.contactapp.dom.seed.country.CountryRefData;
import org.incode.eurocommercial.contactapp.dom.seed.roles.ContactAppAdminRoleAndPermissions;
import org.incode.eurocommercial.contactapp.dom.seed.roles.ContactAppReadOnlyRoleAndPermissions;
import org.incode.eurocommercial.contactapp.dom.seed.roles.ContactAppSuperadminRoleAndPermissions;
import org.incode.eurocommercial.contactapp.dom.seed.roles.ApacheIsisRoleAndPermissions;
import org.incode.eurocommercial.contactapp.dom.seed.users.AdminUser;
import org.incode.eurocommercial.contactapp.dom.seed.users.LockIsisModuleSecurityAdminUser;
import org.incode.eurocommercial.contactapp.dom.seed.users.ReaderUser;
import org.incode.eurocommercial.contactapp.dom.seed.users.SuperadminUser;

@DomainService(
        nature = NatureOfService.DOMAIN
)
@DomainServiceLayout(
        menuOrder = "1100" // not visible, but determines the order initialized (must come after security module's seed service)
)
public class ContactAppRolesAndPermissionsAndRefDataSeedService {

    //region > init
    @Programmatic
    @PostConstruct
    public void init() {
        fixtureScripts.runFixtureScript(new SeedFixtureScript(), null);
    }
    //endregion

    //region  >  (injected)
    @Inject
    FixtureScripts fixtureScripts;
    //endregion

    public static class SeedFixtureScript extends FixtureScript {

        @Override
        protected void execute(final ExecutionContext executionContext) {

            executionContext.executeChild(this, new ContactAppSuperadminRoleAndPermissions());
            executionContext.executeChild(this, new ContactAppAdminRoleAndPermissions());
            executionContext.executeChild(this, new ContactAppReadOnlyRoleAndPermissions());
            executionContext.executeChild(this, new ApacheIsisRoleAndPermissions());

            executionContext.executeChild(this, new SuperadminUser());
            executionContext.executeChild(this, new AdminUser());
            executionContext.executeChild(this, new ReaderUser());
            executionContext.executeChild(this, new LockIsisModuleSecurityAdminUser());

            // configured but not required by any user:
            // executionContext.executeChild(this, new ContactAppFixtureServiceRoleAndPermissions());
            // executionContext.executeChild(this, new SettingsModuleRoleAndPermissions());

            // not configured:
            //            executionContext.executeChild(this, new TogglzModuleAdminRole());
            //            executionContext.executeChild(this, new AuditModuleRoleAndPermissions());
            //            executionContext.executeChild(this, new CommandModuleRoleAndPermissions());
            //            executionContext.executeChild(this, new PublishingModuleRoleAndPermissions());
            //            executionContext.executeChild(this, new SessionLoggerModuleRoleAndPermissions());
            //            executionContext.executeChild(this, new TranslationServicePoMenuRoleAndPermissions());

            executionContext.executeChild(this, new CountryRefData());

        }

    }

}
