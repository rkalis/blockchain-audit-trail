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
package org.incode.eurocommercial.contactapp.dom.number;

import java.util.Set;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.incode.eurocommercial.contactapp.dom.contactable.ContactableEntity;
import org.incode.eurocommercial.contactapp.dom.util.StringUtil;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.DATE_TIME,
        column = "version")
@Queries({
        @Query(
                name = "findByOwnerAndNumber", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.eurocommercial.contactapp.dom.number.ContactNumber "
                        + "WHERE owner == :owner "
                        + "   && number == :number "),
        @Query(
                name = "findByNumber", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.eurocommercial.contactapp.dom.number.ContactNumber "
                        + "WHERE number == :number ")
})
@Unique(name = "ContactNumber_owner_number_UNQ", members = { "owner", "number" })
@DomainObject(
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class ContactNumber implements Comparable<ContactNumber> {

    //region > title
    public static class MaxLength {
        private MaxLength() {
        }

        public static final int TYPE = 20;
        public static final int NUMBER = 30;
    }

    public String title() {
        final StringBuilder buf = new StringBuilder();
        buf.append(getNumber()).append(" (").append(getType()).append(")");
        return buf.toString();
    }
    //endregion

    @Column(allowsNull = "false")
    @Property
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    @Getter @Setter
    private ContactableEntity owner;

    @Column(allowsNull = "false", length = MaxLength.TYPE)
    @Property
    @Getter @Setter
    private String type;

    @Column(allowsNull = "false", length = MaxLength.NUMBER)
    @Property
    @Getter @Setter
    private String number;

    //region > create (action)
    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(position = ActionLayout.Position.PANEL, cssClassFa = "fa fa-plus")
    @MemberOrder(name = "number", sequence = "1")
    public ContactNumber create(
            @Parameter(maxLength = ContactNumber.MaxLength.NUMBER, mustSatisfy = ContactNumberSpec.class)
            final String number,
            @Parameter(maxLength = ContactNumber.MaxLength.TYPE, optionality = Optionality.OPTIONAL)
            final String type,
            @Parameter(maxLength = ContactNumber.MaxLength.TYPE, optionality = Optionality.OPTIONAL)
            final String newType
    ) {
        final ContactNumber newNumber = contactNumberRepository
                .findOrCreate(getOwner(), number, StringUtil.firstNonEmpty(newType, type));
        return newNumber;
    }

    public Set<String> choices1Create() {
        return contactNumberRepository.existingTypes();
    }

    public String default1Create() {
        return ContactNumberType.OFFICE.title();
    }

    public String validateCreate(
            final String number,
            final String type,
            final String newType) {
        return StringUtil.eitherOr(type, newType, "type");
    }

    //endregion

    //region > edit (action)

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(position = ActionLayout.Position.PANEL)
    @MemberOrder(name = "number", sequence = "2")
    public ContactNumber edit(
            @Parameter(maxLength = MaxLength.NUMBER, mustSatisfy = ContactNumberSpec.class)
            final String number,
            @Parameter(maxLength = MaxLength.TYPE, optionality = Optionality.OPTIONAL)
            final String type,
            @Parameter(maxLength = MaxLength.TYPE, optionality = Optionality.OPTIONAL)
            final String newType) {
        setNumber(number);
        setType(StringUtil.firstNonEmpty(newType, type));
        return this;
    }

    public Set<String> choices1Edit() {
        return contactNumberRepository.existingTypes();
    }

    public String default0Edit() {
        return getNumber();
    }

    public String default1Edit() {
        return getType();
    }

    public String validateEdit(
            final String number,
            final String type,
            final String newType) {
        if (number != getNumber() && contactNumberRepository.findByNumber(number) != null) {
            return "A contact number with this number already exists";
        }
        return StringUtil.eitherOr(type, newType, "type");
    }

    //endregion

    //region > delete (action)
    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(position = ActionLayout.Position.PANEL)
    @MemberOrder(name = "number", sequence = "3")
    public ContactableEntity delete() {
        final ContactableEntity owner = getOwner();
        owner.getContactNumbers().remove(this);
        return owner;
    }
    //endregion

    //region > compareTo, toString

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof ContactNumber))
            return false;

        ContactNumber contactNumber = (ContactNumber) obj;
        if (!contactNumber.getNumber().equals(this.getNumber()) || !contactNumber.getType().equals(this.getType())) {
            return false;
        } else {
            return contactNumber.getOwner().equals(this.getOwner());
        }
    }

    @Override
    public int hashCode() {
        return ObjectContracts.hashCode(this, "owner", "number");
    }

    @Override
    public int compareTo(final ContactNumber other) {
        return org.apache.isis.applib.util.ObjectContracts.compare(this, other, "owner", "number");
    }

    @Override
    public String toString() {
        return org.apache.isis.applib.util.ObjectContracts.toString(this, "owner", "number", "type");
    }
    //endregion

    @Inject
    ContactNumberRepository contactNumberRepository;

}
