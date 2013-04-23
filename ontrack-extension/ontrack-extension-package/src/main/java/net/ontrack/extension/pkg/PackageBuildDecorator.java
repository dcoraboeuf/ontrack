package net.ontrack.extension.pkg;

import net.ontrack.core.model.Entity;
import net.ontrack.extension.api.decorator.Decoration;
import net.ontrack.extension.api.decorator.EntityDecorator;
import net.ontrack.extension.api.property.PropertiesService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
public class PackageBuildDecorator implements EntityDecorator {

    private final PropertiesService propertiesService;

    @Autowired
    public PackageBuildDecorator(PropertiesService propertiesService) {
        this.propertiesService = propertiesService;
    }

    /**
     * Decorates only builds
     */
    @Override
    public EnumSet<Entity> getScope() {
        return EnumSet.of(Entity.BUILD);
    }

    /**
     * Returns (if any) the 'package' property for the build
     */
    @Override
    public Decoration getDecoration(Entity entity, int entityId) {
        String pkg = propertiesService.getPropertyValue(entity, entityId, PackageExtension.EXTENSION, PackagePropertyDescriptor.PACKAGE);
        if (StringUtils.isNotBlank(pkg)) {
            return new Decoration(pkg, "info");
        } else {
            return null;
        }
    }
}
