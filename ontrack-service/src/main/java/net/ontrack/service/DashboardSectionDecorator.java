package net.ontrack.service;

import net.ontrack.core.model.Entity;
import net.ontrack.service.model.DashboardSectionDecoration;

public interface DashboardSectionDecorator {

    DashboardSectionDecoration getDecoration(Entity entity, int entityId);
}
