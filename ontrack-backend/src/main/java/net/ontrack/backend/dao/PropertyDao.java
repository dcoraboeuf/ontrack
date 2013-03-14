package net.ontrack.backend.dao;

import net.ontrack.core.model.Entity;

public interface PropertyDao {

    void saveProperty(Entity entity, int entityId, String extension, String name, String value);

}
