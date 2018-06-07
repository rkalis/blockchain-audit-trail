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

import java.util.List;

import javax.inject.Inject;

import com.google.common.base.Splitter;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;
import org.apache.isis.applib.services.metamodel.MetaModelService;

import org.incode.eurocommercial.contactapp.dom.contactable.ContactableEntity;
import org.incode.eurocommercial.contactapp.dom.contacts.Contact;
import org.incode.eurocommercial.contactapp.dom.group.ContactGroup;
import org.incode.eurocommercial.contactapp.dom.number.ContactNumber;
import org.incode.eurocommercial.contactapp.dom.role.ContactRole;

/**
 * Highly abbreviated memento, to reduce network traffic and storage requirements of frontend/mobile app.
 */
@DomainService(nature = NatureOfService.DOMAIN)
public class AbbreviatingMementoService {


    private final BiMap<Class<?>, String> classByAbbreviation = ImmutableBiMap.<Class<?>,String>of(
            Contact.class, "C",
            ContactGroup.class, "CG",
            ContactRole.class, "CR",
            ContactNumber.class, "CN"
    );

    @Programmatic
    public String viewModelMemento(final ViewModelWithUnderlying viewModel) {
        final Bookmark bookmark = bookmarkService.bookmarkFor(viewModel.underlying);
        final String identifier = bookmark.getIdentifier();

        return toAbbreviated(viewModel.underlying.getClass()) + "-" + identifier;
    }

    @Programmatic
    public ContactableEntity viewModelInit(final String memento) {
        final List<String> parts = Splitter.on("-").splitToList(memento);
        final String abbreviatedObjectType = parts.get(0);
        final String identifier = parts.get(1);

        return viewModelInit(abbreviatedObjectType, identifier);
    }

    private ContactableEntity viewModelInit(final String abbreviatedObjectType, final String identifier) {
        final Class<?> cls = fromAbbreviated(abbreviatedObjectType);
        final String objectType = metaModelService.toObjectType(cls);
        final Bookmark bookmark = new Bookmark(objectType, identifier);
        return (ContactableEntity)bookmarkService.lookup(bookmark);
    }

    private String toAbbreviated(final Class<?> cls) {
        return classByAbbreviation.get(cls);
    }

    private Class<?> fromAbbreviated(final String abbreviatedObjectType) {
        return classByAbbreviation.inverse().get(abbreviatedObjectType);
    }

    @Inject BookmarkService bookmarkService;

    @Inject
    MetaModelService metaModelService;


}
