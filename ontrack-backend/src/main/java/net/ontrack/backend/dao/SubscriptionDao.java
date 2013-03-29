package net.ontrack.backend.dao;

import net.ontrack.core.model.Ack;
import net.ontrack.core.model.Entity;

import java.util.List;
import java.util.Map;

public interface SubscriptionDao {

    Ack subscribe(int userId, Entity entity, int entityId);

    List<Integer> findAccountIds (Map<Entity, Integer> entities);

}
