package org.incode.eurocommercial.contactapp.dom.audit;

public final class AuditModule {

    private AuditModule(){}

    public abstract static class ActionDomainEvent<S> extends org.apache.isis.applib.services.eventbus.ActionDomainEvent<S> { }

    public abstract static class CollectionDomainEvent<S,T> extends org.apache.isis.applib.services.eventbus.CollectionDomainEvent<S,T> { }

    public abstract static class PropertyDomainEvent<S,T> extends org.apache.isis.applib.services.eventbus.PropertyDomainEvent<S,T> { }
}
