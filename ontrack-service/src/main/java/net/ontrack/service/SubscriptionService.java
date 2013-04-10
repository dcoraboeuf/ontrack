package net.ontrack.service;

import net.ontrack.core.model.*;

import java.util.Map;
import java.util.Set;

public interface SubscriptionService {

    boolean isEnabled();

    Ack subscribe(Map<Entity, Integer> entities);

    Ack unsubscribe(Map<Entity, Integer> entities);

    void publish(ExpandedEvent event);

    boolean isSubscribed(int accountId, Set<EntityID> entities);

    SubscriptionEntityInfo getSubscriptionEntityInfo(int accountId, Entity entity, int entityId);
}
