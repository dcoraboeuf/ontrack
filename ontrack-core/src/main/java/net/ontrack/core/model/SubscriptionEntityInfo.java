package net.ontrack.core.model;

import lombok.Data;

@Data
public class SubscriptionEntityInfo {

    private final EntityStub entityStub;
    private final String message;

    public static SubscriptionEntityInfo none() {
        return new SubscriptionEntityInfo(null, null);
    }

    public boolean isNone() {
        return entityStub == null;
    }
}
