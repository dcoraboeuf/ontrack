package net.ontrack.extension.api.action;

import net.ontrack.core.model.Entity;

public interface ActionExtension extends TopActionExtension {

    boolean isApplicable(Entity entity, int entityId);
}
