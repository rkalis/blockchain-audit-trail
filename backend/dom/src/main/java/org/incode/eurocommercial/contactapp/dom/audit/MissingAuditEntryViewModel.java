package org.incode.eurocommercial.contactapp.dom.audit;

import java.sql.Timestamp;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.schema.utils.jaxbadapters.JavaSqlTimestampXmlGregorianCalendarAdapter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@XmlRootElement(name = "missingAuditEntry")
@XmlType(
        propOrder = {
                "timestamp",
                "transactionId",
                "sequence",
                "dataHash"
        }
)
@XmlAccessorType(XmlAccessType.FIELD)
@AllArgsConstructor
@NoArgsConstructor
@DomainObject(editing = Editing.DISABLED)
public class MissingAuditEntryViewModel {

    public String title() {
        return transactionId + " [" + sequence + "]";
    }

    @XmlJavaTypeAdapter(JavaSqlTimestampXmlGregorianCalendarAdapter.ForJaxb.class)
    @Getter @Setter
    private Timestamp timestamp;

    @Getter @Setter
    private UUID transactionId;

    @Getter @Setter
    private int sequence;

//    This would be possible to find as well through events
//    @Column(allowsNull = "true")
//    @Getter @Setter
//    private String ethTransactionHash;

    @Getter @Setter
    private String dataHash;
}
