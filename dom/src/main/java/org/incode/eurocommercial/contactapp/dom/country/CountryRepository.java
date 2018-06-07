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
package org.incode.eurocommercial.contactapp.dom.country;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = Country.class
)
public class CountryRepository {

    @Programmatic
    public java.util.List<Country> listAll() {
        return container.allInstances(Country.class);
    }

    @Programmatic
    public Country findByName(
            final String name
    ) {
        return container.uniqueMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        Country.class,
                        "findByName",
                        "name", name));
    }

    @Programmatic
    public Country create(final String name) {
        final Country country = container.newTransientInstance(Country.class);
        country.setName(name);
        container.persistIfNotAlready(country);
        return country;
    }

    @Programmatic
    public Country findOrCreate(
            final String name
    ) {
        Country country = findByName(name);
        if (country == null) {
            country = create(name);
        }
        return country;
    }

    @javax.inject.Inject
    org.apache.isis.applib.DomainObjectContainer container;
}
