package net.ontrack.extension.api.action;

import net.ontrack.core.model.Entity;
import net.ontrack.core.model.EntitySummary;
import net.sf.jstring.Localizable;

public interface EntityActionExtension<T extends EntitySummary> {

    Entity getScope();

    String getExtension();

    String getName();

    String getRole(T summary);

    boolean isEnabled(T summary);

    String getPath(T summary);

    Localizable getTitle(T summary);

    String getIcon(T summary);

    String getCss(T summary);

}
