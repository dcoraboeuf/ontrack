package net.ontrack.extension.api.decorator;

import net.ontrack.core.model.Decoration;
import net.ontrack.core.model.Entity;

import java.util.EnumSet;

public interface EntityDecorator {

    /**
     * Scope of the decorator
     *
     * @return List of {@link net.ontrack.core.model.Entity} this decorator can apply to
     */
    EnumSet<Entity> getScope();

    /**
     * Gets a decoration for this entity.
     *
     * @param entity   Entity type
     * @param entityId Entity ID
     * @return A decoration to apply or <code>null</code> if none.
     */
    Decoration getDecoration(Entity entity, int entityId);

}
