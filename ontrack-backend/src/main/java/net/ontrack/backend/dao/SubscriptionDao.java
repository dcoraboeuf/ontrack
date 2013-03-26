package net.ontrack.backend.dao;

import net.ontrack.core.model.Ack;
import net.ontrack.core.model.Entity;

public interface SubscriptionDao {

    Ack subscribe(int userId, Entity entity, int entityId);

}
