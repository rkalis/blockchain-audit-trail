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
