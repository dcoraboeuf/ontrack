package net.ontrack.extension.api.configuration;

import java.util.List;

public interface ConfigurationExtension {

    /**
     * Name of the associated extension
     *
     * @return Extension ID
     */
    String getExtension();

    /**
     * Name for this configuration
     */
    String getName();

    /**
     * Localization key for the title of this configuration
     */
    String getTitleKey();

    /**
     * List of fields for the configuration
     */
    List<? extends ConfigurationExtensionField> getFields();

    /**
     * Fills the configuration
     */
    void configure(String name, String value);
}
