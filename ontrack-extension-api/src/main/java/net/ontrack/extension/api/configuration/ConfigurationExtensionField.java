package net.ontrack.extension.api.configuration;

public interface ConfigurationExtensionField {

    String getName();

    String getDisplayNameKey();

    String getDefaultValue();

    String getValue();

    String getType();

    void validate (String value);

}
