package net.ontrack.service;

import net.ontrack.core.model.Ack;
import net.ontrack.core.model.Entity;
import net.ontrack.service.model.Event;

import java.util.Map;

public interface SubscriptionService {

    boolean isEnabled();

    Ack subscribe(Map<Entity, Integer> entities);

    void publish(Event event);
}
