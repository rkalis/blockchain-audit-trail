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
package org.incode.eurocommercial.contactapp.dom.contactable;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberGroupLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.incode.eurocommercial.contactapp.dom.ContactAppDomainModule;
import org.incode.eurocommercial.contactapp.dom.number.ContactNumber;
import org.incode.eurocommercial.contactapp.dom.number.ContactNumberRepository;
import org.incode.eurocommercial.contactapp.dom.number.ContactNumberSpec;
import org.incode.eurocommercial.contactapp.dom.number.ContactNumberType;
import org.incode.eurocommercial.contactapp.dom.util.StringUtil;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@Version(
        strategy = VersionStrategy.DATE_TIME,
        column = "version")
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.CLASS_NAME,
        column = "discriminator")
@Queries({
})
@DomainObject(
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
@MemberGroupLayout(
        columnSpans = { 6, 0, 0, 6 }
)

@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class ContactableEntity {

    //region > title

    public static class MaxLength {
        private MaxLength() {
        }

        public static final int NAME = 50;
        public static final int EMAIL = 50;
        public static final int NOTES = ContactAppDomainModule.MaxLength.NOTES;
    }

    public String title() {
        return getName();
    }

    //endregion

    @Column(allowsNull = "false", length = MaxLength.NAME)
    @Property
    @Getter @Setter
    private String name;

    @Column(allowsNull = "true", length = MaxLength.EMAIL)
    @Property
    @Getter @Setter
    private String email;

    @Column(allowsNull = "true", length = MaxLength.NOTES)
    @Property
    @PropertyLayout(multiLine = 6, hidden = Where.ALL_TABLES)
    @Getter @Setter
    private String notes;

    @Persistent(mappedBy = "owner", dependentElement = "true")
    @Collection()
    @CollectionLayout(render = RenderType.EAGERLY)
    @Getter @Setter
    private SortedSet<ContactNumber> contactNumbers = new TreeSet<ContactNumber>();

    //region > addContactNumber (action)
    //endregion

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(named = "Add")
    @MemberOrder(name = "contactNumbers", sequence = "1")
    public ContactableEntity addContactNumber(
            @Parameter(maxLength = ContactNumber.MaxLength.NUMBER, mustSatisfy = ContactNumberSpec.class)
            final String number,
            @Parameter(maxLength = ContactNumber.MaxLength.TYPE, optionality = Optionality.OPTIONAL)
            final String type,
            @Parameter(maxLength = ContactNumber.MaxLength.TYPE, optionality = Optionality.OPTIONAL)
            final String newType
    ) {
        contactNumberRepository.findOrCreate(this, number, StringUtil.firstNonEmpty(newType, type));
        return this;
    }

    public Set<String> choices1AddContactNumber() {
        return contactNumberRepository.existingTypes();
    }

    public String default1AddContactNumber() {
        return ContactNumberType.OFFICE.title();
    }

    public String validateAddContactNumber(
            final String number,
            final String type,
            final String newType) {

        return StringUtil.eitherOr(type, newType, "type");
    }

    //region > removeContactNumber (action)
    //endregion

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(named = "Remove")
    @MemberOrder(name = "contactNumbers", sequence = "2")
    public ContactableEntity removeContactNumber(final String number) {
        final Optional<ContactNumber> contactNumberIfAny = Iterables
                .tryFind(getContactNumbers(), cn -> Objects.equal(cn.getNumber(), number));

        if (contactNumberIfAny.isPresent()) {
            getContactNumbers().remove(contactNumberIfAny.get());
        }
        return this;
    }

    public String disableRemoveContactNumber() {
        return getContactNumbers().isEmpty() ? "No contact numbers to remove" : null;
    }

    public List<String> choices0RemoveContactNumber() {
        return Lists.transform(Lists.newArrayList(getContactNumbers()), ContactNumber::getNumber);
    }

    public String default0RemoveContactNumber() {
        final List<String> choices = choices0RemoveContactNumber();
        return choices.isEmpty() ? null : choices.get(0);
    }

    //region > helpers


    @Override public boolean equals(final Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof ContactableEntity))
            return false;

        ContactableEntity contactableEntity = (ContactableEntity) obj;
        return contactableEntity.getName().equals(this.getName());
    }

    @Override
    public String toString() {
        return org.apache.isis.applib.util.ObjectContracts.toString(this, "name");
    }

    public static <T extends ContactableEntity> Function<T, String> nameOf() {
        return new Function<T, String>() {
            @Nullable @Override
            public String apply(final T contactGroup) {
                return contactGroup.getName();
            }
        };
    }

    //endregion

    //region > injected services
    //endregion

    @Inject
    ContactNumberRepository contactNumberRepository;


}
