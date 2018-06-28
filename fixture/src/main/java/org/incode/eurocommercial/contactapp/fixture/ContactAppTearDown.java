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
package org.incode.eurocommercial.contactapp.fixture;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

import org.incode.eurocommercial.contactapp.dom.audit.AuditEntry;
import org.incode.eurocommercial.contactapp.dom.audit.ChangedProperty;
import org.incode.eurocommercial.contactapp.dom.contacts.Contact;
import org.incode.eurocommercial.contactapp.dom.country.Country;
import org.incode.eurocommercial.contactapp.dom.group.ContactGroup;
import org.incode.eurocommercial.contactapp.dom.number.ContactNumber;
import org.incode.eurocommercial.contactapp.dom.role.ContactRole;

public class ContactAppTearDown extends FixtureScript {

    @Override
    protected void execute(final ExecutionContext executionContext) {
        deleteAllDirect();
    }

    protected void deleteAllDirect() {
        deleteFrom(ContactNumber.class);
        deleteFrom(ContactRole.class);
        deleteFrom(Contact.class);
        deleteFrom(ContactGroup.class);
        deleteFrom(Country.class);
        deleteFrom(ChangedProperty.class);
        deleteFrom(AuditEntry.class);
    }

    protected void deleteFrom(final Class cls) {
        preDeleteFrom(cls);
        deleteFrom(cls.getSimpleName());
        postDeleteFrom(cls);
    }

    protected void deleteFrom(final String table) {
        isisJdoSupport.executeUpdate("DELETE FROM " + "\"" + table + "\"");
    }

    protected void preDeleteFrom(final Class cls) {}

    protected void postDeleteFrom(final Class cls) {}

    @Inject
    private IsisJdoSupport isisJdoSupport;

}
