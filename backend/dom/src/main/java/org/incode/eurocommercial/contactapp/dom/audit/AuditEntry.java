package org.incode.eurocommercial.contactapp.dom.audit;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

import org.apache.isis.applib.services.HasTransactionId;
import org.apache.isis.applib.services.HasUsername;

import lombok.Getter;
import lombok.Setter;

public class AuditEntry implements HasTransactionId, HasUsername {
    public static AuditEntry deserialise(String representation) {
        return new Gson().fromJson(representation, AuditEntry.class);
    }

    public String serialise() {
        return new Gson().toJson(this);
    }

    @Getter @Setter
    private String user;

    @Override
    public String getUsername() {
        return user;
    }

    @Getter @Setter
    private long timestamp;

    @Getter @Setter
    private UUID transactionId;

    @Getter @Setter
    private int sequence;

    @Getter
    private List<ChangedObject> changedObjects = Lists.newArrayList();;

    public static class ChangedObject {
        @Getter @Setter
        private String target;

        @Getter
        private List<ChangedProperty> changedProperties = Lists.newArrayList();

        public static class ChangedProperty {
            @Getter @Setter
            private String property;

            @Getter @Setter
            private String preValue;

            @Getter @Setter
            private String postValue;
        }
    }

    public void addChange(String target, String property, String preValue, String postValue) {
        ChangedObject changedObject = Iterables.getLast(changedObjects, null);
        if (changedObject == null || !changedObject.target.equals(target)) {
            changedObject = new ChangedObject();
            changedObject.target = target;
            changedObjects.add(changedObject);
        }
        ChangedObject.ChangedProperty changedProperty = new ChangedObject.ChangedProperty();
        changedProperty.property = property;
        changedProperty.preValue = preValue;
        changedProperty.postValue = postValue;
        changedObject.changedProperties.add(changedProperty);
    }

//    @Override
//    public String toString() {
//        return ObjectContracts.toString(this, "timestamp,user,targetStr,memberIdentifier");
//    }
}
