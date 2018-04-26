package org.incode.eurocommercial.contactapp.dom.audit;

import org.apache.isis.applib.AppManifestAbstract;

public class AuditSpiAppManifest extends AppManifestAbstract {

    public static final Builder BUILDER = Builder.forModules(
            AuditModule.class
    );

    public AuditSpiAppManifest() {
        super(BUILDER);
    }

}
