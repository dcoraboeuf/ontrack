package net.ontrack.extension.api.decorator;

import net.ontrack.core.model.Decoration;
import net.ontrack.core.model.Entity;

import java.util.List;

public interface DecorationService {

    List<Decoration> getDecorations(Entity entity, int entityId);

}
