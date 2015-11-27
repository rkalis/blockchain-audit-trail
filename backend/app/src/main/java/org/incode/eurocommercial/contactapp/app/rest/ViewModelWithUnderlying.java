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
package org.incode.eurocommercial.contactapp.app.rest;

import javax.inject.Inject;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.ViewModel;

public abstract class ViewModelWithUnderlying<T> implements ViewModel {

    protected T underlying;

    @Override
    public String viewModelMemento() {
        return mementoService.viewModelMemento(this);
    }

    @Override
    public void viewModelInit(final String memento) {
        this.underlying = (T) mementoService.viewModelInit(memento);
    }

    public String title() {
        return container.titleOf(underlying);
    }

    @Override
    public String toString() {
        return underlying != null? underlying.toString(): "(no underlying)";
    }


    @Inject
    protected DomainObjectContainer container;

    @Inject
    protected AbbreviatingMementoService mementoService;
}
