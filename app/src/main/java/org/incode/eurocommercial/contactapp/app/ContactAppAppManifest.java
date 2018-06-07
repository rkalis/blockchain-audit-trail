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
package org.incode.eurocommercial.contactapp.app;

import com.google.common.base.Joiner;

import org.apache.isis.applib.AppManifestAbstract;

import org.isisaddons.metamodel.paraname8.NamedFacetOnParameterParaname8Factory;
import org.isisaddons.module.security.facets.TenantedAuthorizationFacetFactory;

import org.incode.eurocommercial.contactapp.dom.ContactAppDomainModule;
import org.incode.eurocommercial.contactapp.fixture.ContactAppFixtureModule;

public class ContactAppAppManifest extends AppManifestAbstract {

    public static final Builder BUILDER = Builder.forModules(
            ContactAppDomainModule.class     // entities and domain services
            ,ContactAppFixtureModule.class   // fixture scripts and FixtureScriptsSpecificationProvider
            ,ContactAppAppModule.class       // ContactAppRolesAndPermissionsSeedService (requires security module)

            ,org.isisaddons.module.excel.ExcelModule.class // to run fixtures
            ,org.isisaddons.module.settings.SettingsModule.class // used by ContactAppUserSettingsThemeProvider
            ,org.isisaddons.module.security.SecurityModule.class

            // not required at the moment...

            //                ,org.isisaddons.module.audit.AuditModule.class
            //                ,org.isisaddons.module.command.CommandModule.class
            //                ,org.isisaddons.module.devutils.DevUtilsModule.class
            //                ,org.isisaddons.module.docx.DocxModule.class
            //                ,org.isisaddons.module.fakedata.FakeDataModule.class
            //                ,org.isisaddons.module.publishing.PublishingModule.class
            //                ,org.isisaddons.module.sessionlogger.SessionLoggerModule.class
            //                ,org.incode.module.note.dom.NoteModule.class
            //                ,org.incode.module.commchannel.dom.CommChannelModule.class
    ).withAdditionalServices(
            org.isisaddons.module.security.dom.password.PasswordEncryptionServiceUsingJBcrypt.class
    ).withAuthMechanism("shiro")
    .withConfigurationProperty(
            "isis.reflector.facets.include",
            Joiner.on(',').join(
                    NamedFacetOnParameterParaname8Factory.class.getName()
                    , TenantedAuthorizationFacetFactory.class.getName()
            )
    );

    public ContactAppAppManifest() {
        super(BUILDER);
    }

}
