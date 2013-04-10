package net.ontrack.backend.dao;

import net.ontrack.core.model.Ack;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.EntityID;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SubscriptionDao {

    Ack subscribe(int userId, Entity entity, int entityId);

    List<Integer> findAccountIds (Map<Entity, Integer> entities);

    Set<EntityID> findEntitiesByAccount(int accountId);

    Ack unsubscribe(int userId, Entity entity, int entityId);
}
