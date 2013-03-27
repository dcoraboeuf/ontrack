package net.ontrack.extension.api.support;

import net.ontrack.extension.api.Extension;
import net.ontrack.extension.api.action.ActionExtension;
import net.ontrack.extension.api.configuration.ConfigurationExtension;
import net.ontrack.extension.api.property.PropertyExtensionDescriptor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class ExtensionAdapter implements Extension {

    private final String name;
    private final List<? extends PropertyExtensionDescriptor> propertyExtensionDescriptors;
    private final List<? extends ConfigurationExtension> configurationExtensions;
    private final Collection<? extends ActionExtension> topLevelActions;
    private final Collection<? extends ActionExtension> diffActions;

    protected ExtensionAdapter(String name, List<? extends PropertyExtensionDescriptor> propertyExtensionDescriptors) {
        this(name, propertyExtensionDescriptors, Collections.<ConfigurationExtension>emptyList());
    }

    protected ExtensionAdapter(String name, List<? extends PropertyExtensionDescriptor> propertyExtensionDescriptors, List<? extends ConfigurationExtension> configurationExtensions) {
        this(name, propertyExtensionDescriptors, configurationExtensions, Collections.<ActionExtension>emptyList());
    }

    protected ExtensionAdapter(String name, List<? extends PropertyExtensionDescriptor> propertyExtensionDescriptors, List<? extends ConfigurationExtension> configurationExtensions, Collection<? extends ActionExtension> topLevelActions) {
        this(name, propertyExtensionDescriptors, configurationExtensions, topLevelActions, Collections.<ActionExtension>emptyList());
    }

    protected ExtensionAdapter(String name, List<? extends PropertyExtensionDescriptor> propertyExtensionDescriptors, List<? extends ConfigurationExtension> configurationExtensions, Collection<? extends ActionExtension> topLevelActions, Collection<? extends ActionExtension> diffActions) {
        this.name = name;
        this.propertyExtensionDescriptors = propertyExtensionDescriptors;
        this.configurationExtensions = configurationExtensions;
        this.topLevelActions = topLevelActions;
        this.diffActions = diffActions;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<? extends PropertyExtensionDescriptor> getPropertyExtensionDescriptors() {
        return propertyExtensionDescriptors;
    }

    @Override
    public List<? extends ConfigurationExtension> getConfigurationExtensions() {
        return configurationExtensions;
    }

    @Override
    public Collection<? extends ActionExtension> getTopLevelActions() {
        return topLevelActions;
    }

    @Override
    public Collection<? extends ActionExtension> getDiffActions() {
        return diffActions;
    }
}
