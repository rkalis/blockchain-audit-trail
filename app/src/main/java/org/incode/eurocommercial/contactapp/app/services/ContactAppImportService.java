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
package org.incode.eurocommercial.contactapp.app.services;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.fixturescripts.FixtureResult;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.core.runtime.system.context.IsisContext;

import org.isisaddons.module.excel.dom.ExcelFixture;

import org.incode.eurocommercial.contactapp.dom.contacts.ContactRepository;
import org.incode.eurocommercial.contactapp.fixture.scenarios.demo.ContactImport;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.TERTIARY
)
public class ContactAppImportService {

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(cssClassFa = "fa-upload")
    public List<FixtureResult> importSpreadsheet(final Blob file) {

        FixtureScript script = new ExcelFixture(
                file,
                ContactImport.class
        );
        return fixtureScripts.runFixtureScript(script, "");
    }

    public String disableImportSpreadsheet() {
        return !contactRepository.listAll().isEmpty() ? "Contacts have already been imported": null;
    }

    @Inject
    private ContactRepository contactRepository;

    @Inject
    private FixtureScripts fixtureScripts;

    @Inject
    private IsisContext isisContext;

    @Inject
    private DomainObjectContainer container;

}