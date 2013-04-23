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

    protected ExtensionAdapter(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<? extends PropertyExtensionDescriptor> getPropertyExtensionDescriptors() {
        return Collections.emptyList();
    }

    @Override
    public List<? extends ConfigurationExtension> getConfigurationExtensions() {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends ActionExtension> getTopLevelActions() {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends ActionExtension> getDiffActions() {
        return Collections.emptyList();
    }
}
