package net.ontrack.service;

import net.ontrack.core.model.Ack;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.EntityID;
import net.ontrack.core.model.ExpandedEvent;

import java.util.Map;
import java.util.Set;

public interface SubscriptionService {

    boolean isEnabled();

    Ack subscribe(Map<Entity, Integer> entities);

    void publish(ExpandedEvent event);

    boolean isSubscribed(int id, Set<EntityID> entities);
}
