package net.ontrack.service;

import net.ontrack.core.model.DashboardStatus;
import net.ontrack.core.model.Entity;

public interface DashboardStatusProvider {

    boolean apply(Entity entity, int entityId);

    DashboardStatus getStatus(Entity entity, int entityId);

}
