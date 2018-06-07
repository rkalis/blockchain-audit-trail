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
package org.incode.eurocommercial.contactapp.dom.seed.roles;

import org.isisaddons.module.security.dom.permission.ApplicationPermissionMode;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionRule;
import org.isisaddons.module.security.seed.scripts.AbstractRoleAndPermissionsFixtureScript;

import org.incode.eurocommercial.contactapp.dom.contacts.Contact;
import org.incode.eurocommercial.contactapp.dom.contacts.ContactMenu;
import org.incode.eurocommercial.contactapp.dom.country.Country;
import org.incode.eurocommercial.contactapp.dom.group.ContactGroup;
import org.incode.eurocommercial.contactapp.dom.group.ordering.ContactGroupOrderingViewModel;
import org.incode.eurocommercial.contactapp.dom.group.ordering.ContactGroup_fixDisplayOrder;
import org.incode.eurocommercial.contactapp.dom.number.ContactNumber;
import org.incode.eurocommercial.contactapp.dom.role.ContactRole;

public class ContactAppReadOnlyRoleAndPermissions extends AbstractRoleAndPermissionsFixtureScript {

    public static final String ROLE_NAME = "contactapp-readonly-role";

    public ContactAppReadOnlyRoleAndPermissions() {
        super(ROLE_NAME, "Read-only access to contactapp domain objects and home page");
    }

    @Override
    protected void execute(final ExecutionContext executionContext) {
        // dom
        newMemberPermissions(
                ApplicationPermissionRule.ALLOW,
                ApplicationPermissionMode.CHANGING,
                ContactMenu.class,
                "listAll",
                "find",
                "findByGroup",
                "findByRole");
        newClassPermissions(
                ApplicationPermissionRule.ALLOW,
                ApplicationPermissionMode.VIEWING,
                Contact.class,
                ContactGroup.class,
                Country.class,
                ContactRole.class,
                ContactNumber.class);

        // display order view model
        newClassPermissions(
                ApplicationPermissionRule.ALLOW,
                ApplicationPermissionMode.CHANGING,
                ContactGroup_fixDisplayOrder.class);
        newClassPermissions(
                ApplicationPermissionRule.ALLOW,
                ApplicationPermissionMode.VIEWING,
                ContactGroupOrderingViewModel.class);

        // home page
        newMemberPermissions(
                ApplicationPermissionRule.ALLOW,
                ApplicationPermissionMode.CHANGING,
                classFor("org.incode.eurocommercial.contactapp.app.services.homepage.HomePageService"),
                "homePage");
        newClassPermissions(
                ApplicationPermissionRule.ALLOW,
                ApplicationPermissionMode.VIEWING,
                classFor("org.incode.eurocommercial.contactapp.app.services.homepage.HomePageViewModel"));

        // rest access (frontend mobile)
        newPackagePermissions(
                ApplicationPermissionRule.ALLOW,
                ApplicationPermissionMode.CHANGING,
                "org.incode.eurocommercial.contactapp.app.rest.v1"
        );
    }

    private static Class<?> classFor(final String className) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
