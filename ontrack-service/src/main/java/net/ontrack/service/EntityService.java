package net.ontrack.service;

import net.ontrack.core.model.Entity;
import net.ontrack.core.model.EntityStub;

import java.util.Map;

public interface EntityService {

    int getEntityId(Entity entity, String name, Map<Entity, Integer> parentIds);

    String getEntityName(Entity entity, int entityId);

    EntityStub getEntityStub(Entity entity, int entityId);

}
