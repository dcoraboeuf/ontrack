package net.ontrack.backend.extension;

import net.ontrack.backend.dao.ConfigurationDao;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.api.configuration.ConfigurationExtension;
import net.ontrack.extension.api.configuration.ConfigurationExtensionField;
import net.ontrack.extension.api.configuration.ConfigurationExtensionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;

@Service
public class DefaultConfigurationExtensionService implements ConfigurationExtensionService {

    private final ExtensionManager extensionManager;
    private final ConfigurationDao configurationDao;

    @Autowired
    public DefaultConfigurationExtensionService(ExtensionManager extensionManager, ConfigurationDao configurationDao) {
        this.extensionManager = extensionManager;
        this.configurationDao = configurationDao;
    }

    @Override
    public Collection<? extends ConfigurationExtension> getConfigurationExtensions() {
        return extensionManager.getConfigurationExtensions();
    }

    // FIXME Loads all the configurations at start-up

    @Override
    @Transactional
    public String saveExtensionConfiguration(String extension, String name, Map<String, String> parameters) {
        // Gets the configuration extension
        ConfigurationExtension configurationExtension = extensionManager.getConfigurationExtension(extension, name);
        // For all fields
        for (ConfigurationExtensionField field : configurationExtension.getFields()) {
            // Gets the new value
            String value = parameters.get(field.getName());
            // Controls the value
            field.validate(value);
            // Configuration key
            String key = String.format("x-%s-%s-%s", extension, name, field.getName());
            // Saves the value
            configurationDao.setValue(key, value);
        }
        // FIXME Updates the configuration in memory
        // OK
        return configurationExtension.getTitleKey();
    }
}
