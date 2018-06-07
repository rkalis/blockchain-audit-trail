/**
 * Copyright 2018 Rosco Kalis
 * <p>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.incode.eurocommercial.contactapp.dom.audit;

import java.util.Comparator;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.xml.bind.annotation.XmlTransient;

import com.google.gson.annotations.Expose;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.Where;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType= IdentityType.DATASTORE
)
@DomainObject(
        editing = Editing.DISABLED,
        publishing = Publishing.DISABLED
)
public class ChangedProperty implements Comparable<ChangedProperty> {
    public String title() {
        return target + " " + property;
    }

    @Column(allowsNull = "false")
    @XmlTransient
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    @Getter @Setter
    private AuditEntry auditEntry;

    @Expose
    @Column(allowsNull = "false")
    @Getter @Setter
    private String target;

    @Expose
    @Column(allowsNull = "false")
    @Getter @Setter
    private String property;

    @Expose
    @Column(allowsNull = "true", jdbcType = "CLOB")
    @Getter @Setter
    private String preValue;

    @Expose
    @Column(allowsNull = "true", jdbcType = "CLOB")
    @Getter @Setter
    private String postValue;

    @Override
    public int compareTo(final ChangedProperty other) {
        return Comparator.comparing(ChangedProperty::getAuditEntry)
                .thenComparing(ChangedProperty::getTarget)
                .thenComparing(ChangedProperty::getProperty)
                .compare(this, other);
    }
}
