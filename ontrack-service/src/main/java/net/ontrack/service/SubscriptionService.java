package net.ontrack.service;

import net.ontrack.core.model.Ack;
import net.ontrack.core.model.Entity;

import java.util.Map;

public interface SubscriptionService {

    boolean isEnabled();

    Ack subscribe(Map<Entity, Integer> entities);
}
