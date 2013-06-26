package net.ontrack.extension.api.action;

import net.ontrack.core.model.Entity;

public interface ActionExtension {

    String getExtension();

    String getName();

    String getRole();

    String getPath();

    String getTitleKey();

    boolean isApplicable(Entity entity, int entityId);
}
