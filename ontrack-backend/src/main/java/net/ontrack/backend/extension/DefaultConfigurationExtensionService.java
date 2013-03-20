package net.ontrack.backend.extension;

import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.api.configuration.ConfigurationExtension;
import net.ontrack.extension.api.configuration.ConfigurationExtensionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class DefaultConfigurationExtensionService implements ConfigurationExtensionService {

    private final ExtensionManager extensionManager;

    @Autowired
    public DefaultConfigurationExtensionService(ExtensionManager extensionManager) {
        this.extensionManager = extensionManager;
    }

    @Override
    public Collection<? extends ConfigurationExtension> getConfigurationExtensions() {
        return extensionManager.getConfigurationExtensions();
    }

}
