package net.ontrack.extension.api.configuration;

public abstract class AbstractConfigurationExtensionField implements ConfigurationExtensionField {

    private final String name;
    private final String displayNameKey;
    private final String type;
    private final String defaultValue;
    private final String value;

    protected AbstractConfigurationExtensionField(String name, String displayNameKey, String type, String defaultValue, String value) {
        this.name = name;
        this.displayNameKey = displayNameKey;
        this.type = type;
        this.defaultValue = defaultValue;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayNameKey() {
        return displayNameKey;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void validate(String value) {
    }
}
