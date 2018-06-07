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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.isis.applib.annotation.CollectionLayout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "validationResult")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        propOrder = {
                "validatedAuditEntries",
                "invalidatedAuditEntries",
                "missingAuditEntries"
        }
)
@AllArgsConstructor
@NoArgsConstructor
public class ValidationReport {
    public String title() {
        return "Validation Report";
    }

    @XmlElementWrapper
    @CollectionLayout(defaultView = "table")
    @Getter
    private List<AuditEntry> validatedAuditEntries;

    @XmlElementWrapper
    @CollectionLayout(defaultView = "table")
    @Getter
    private List<AuditEntry> invalidatedAuditEntries;

    @XmlElementWrapper
    @CollectionLayout(defaultView = "table")
    @Getter
    private List<MissingAuditEntryViewModel> missingAuditEntries;
}
