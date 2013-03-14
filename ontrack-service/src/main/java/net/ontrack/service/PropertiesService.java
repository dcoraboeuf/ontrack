package net.ontrack.service;

import net.ontrack.core.model.Entity;
import net.ontrack.core.model.PropertiesCreationForm;

public interface PropertiesService {

    void createProperties(Entity entity, int entityId, PropertiesCreationForm properties);

}
