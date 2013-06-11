package net.ontrack.service;

import net.ontrack.core.model.DashboardSection;
import net.ontrack.core.model.Entity;

public interface DashboardSectionProvider {

    boolean apply(Entity entity, int entityId);

    DashboardSection getSection(Entity entity, int entityId);

}
