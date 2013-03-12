package net.ontrack.backend.dao;

import net.ontrack.core.model.Entity;

import java.util.Map;

public interface EntityDao {

    int getEntityId(Entity entity, String name, Map<Entity, Integer> parentIds);

    Integer getParentEntityId(Entity parentEntity, Entity entity, int entityId);
}
