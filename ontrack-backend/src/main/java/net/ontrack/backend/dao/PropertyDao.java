package net.ontrack.backend.dao;

import net.ontrack.backend.dao.model.TProperty;
import net.ontrack.core.model.Entity;

import java.util.List;

public interface PropertyDao {

    void saveProperty(Entity entity, int entityId, String extension, String name, String value);

    TProperty findByExtensionAndName(Entity entity, int entityId, String extension, String name);

    List<TProperty> findAll(Entity entity, int entityId);
}
