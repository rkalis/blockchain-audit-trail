package org.incode.eurocommercial.contactapp.dom.audit;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@XmlRootElement
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
    @Getter
    private List<AuditEntry> validatedAuditEntries;

    @Getter
    private List<AuditEntry> invalidatedAuditEntries;

    @Getter
    private List<AuditEntry> missingAuditEntries;
}
