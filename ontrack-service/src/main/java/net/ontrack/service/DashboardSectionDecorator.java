package net.ontrack.service;

import net.ontrack.core.model.Entity;

import java.util.Collection;

public interface DashboardSectionDecorator {

    Collection<String> getClasses(Entity entity, int entityId);

}
