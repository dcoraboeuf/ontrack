package net.ontrack.core.model;

import lombok.Data;

import java.util.Collection;

@Data
public class SubscriptionsForUser {

    private final int id;
    private final String name;
    private final String fullName;
    private final Collection<SubscriptionEntityInfo> subscriptions;

}
