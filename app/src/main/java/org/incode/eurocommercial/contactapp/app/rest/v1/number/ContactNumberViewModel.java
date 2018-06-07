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
package org.incode.eurocommercial.contactapp.app.rest.v1.number;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.base.Function;

import org.apache.isis.applib.DomainObjectContainer;

import org.incode.eurocommercial.contactapp.app.rest.ViewModelWithUnderlying;
import org.incode.eurocommercial.contactapp.dom.number.ContactNumber;

public class ContactNumberViewModel extends ViewModelWithUnderlying<ContactNumber> {

    public static Function<ContactNumber, ContactNumberViewModel> create(final DomainObjectContainer container) {
        return new Function<ContactNumber, ContactNumberViewModel>() {
            @Nullable @Override public ContactNumberViewModel apply(@Nullable final ContactNumber input) {
                return input != null? container.injectServicesInto(new ContactNumberViewModel(input)): null;
            }
        };
    }

    public ContactNumberViewModel() {
    }

    public ContactNumberViewModel(final ContactNumber underlying) {
        this.underlying = underlying;
    }

    @XmlTransient
    public String getType() {
        return underlying.getType();
    }

    @XmlTransient
    public String getNumber() {
        return underlying.getNumber();
    }

}
