package net.ontrack.extension.api;

import net.ontrack.core.model.Entity;
import net.ontrack.extension.api.action.ActionExtension;
import net.ontrack.extension.api.action.EntityActionExtension;
import net.ontrack.extension.api.configuration.ConfigurationExtension;
import net.ontrack.extension.api.decorator.EntityDecorator;
import net.ontrack.extension.api.property.PropertyExtensionDescriptor;

import java.util.Collection;
import java.util.List;

public interface Extension {

    String getName();

    Collection<String> getDependencies();

    List<? extends PropertyExtensionDescriptor> getPropertyExtensionDescriptors();

    List<? extends ConfigurationExtension> getConfigurationExtensions();

    Collection<? extends ActionExtension> getTopLevelActions();

    Collection<? extends ActionExtension> getDiffActions();

    Collection<? extends EntityActionExtension> getEntityActions();

    Collection<? extends EntityDecorator> getDecorators();

    String getExtensionStyle(String scope);
}
