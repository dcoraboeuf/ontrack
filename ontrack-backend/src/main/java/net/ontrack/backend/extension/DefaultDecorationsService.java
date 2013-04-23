package net.ontrack.backend.extension;

import net.ontrack.core.model.Decoration;
import net.ontrack.core.model.Entity;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.api.decorator.DecorationService;
import net.ontrack.extension.api.decorator.EntityDecorator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class DefaultDecorationsService implements DecorationService {

    private final ExtensionManager extensionManager;

    @Autowired
    public DefaultDecorationsService(ExtensionManager extensionManager) {
        this.extensionManager = extensionManager;
    }

    @Override
    public List<Decoration> getDecorations(Entity entity, int entityId) {
        Collection<? extends EntityDecorator> decorators = extensionManager.getDecorators();
        List<Decoration> decorations = new ArrayList<>();
        // Gets all decorators
        for (EntityDecorator decorator : decorators) {
            if (decorator.getScope().contains(entity)) {
                Decoration decoration = decorator.getDecoration(entity, entityId);
                if (decoration != null) {
                    decorations.add(decoration);
                }
            }
        }
        // TODO Sorting
        // OK
        return decorations;
    }
}
