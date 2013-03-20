package net.ontrack.extension.api.configuration;

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
}
