package net.ontrack.extension.general;

import net.ontrack.core.model.Decoration;
import net.ontrack.core.model.Entity;
import net.ontrack.extension.api.decorator.EntityDecorator;
import net.ontrack.service.ManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
public class ValidationStampWeatherDecorator implements EntityDecorator {

    private final ManagementService managementService;

    @Autowired
    public ValidationStampWeatherDecorator(ManagementService managementService) {
        this.managementService = managementService;
    }

    @Override
    public EnumSet<Entity> getScope() {
        return EnumSet.of(Entity.VALIDATION_STAMP);
    }

    @Override
    public Decoration getDecoration(Entity entity, int entityId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
