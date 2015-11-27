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

import javax.inject.Inject;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.core.runtime.system.context.IsisContext;

import org.incode.eurocommercial.contactapp.app.services.homepage.HomePageViewModel;
import org.incode.eurocommercial.contactapp.dom.contacts.ContactRepository;
import org.incode.eurocommercial.contactapp.fixture.ContactAppTearDown;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.TERTIARY
)
public class ContactAppTearDownService {

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(cssClassFa = "fa-upload")
    public HomePageViewModel tearDownData(@ParameterLayout(named = "I am aware that this action will completely and irrevocably delete all data from the database") boolean aware,
                                          @ParameterLayout(named = "I hereby confirm that this is my intention") boolean confirm) {
        FixtureScript script = new ContactAppTearDown();
        fixtureScripts.runFixtureScript(script, "");
        return new HomePageViewModel();
    }

    public String validateTearDownData(boolean aware, boolean confirm) {
        return aware && confirm ? null : "You must confirm that your are aware of the implications of this action and this is your intention";

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