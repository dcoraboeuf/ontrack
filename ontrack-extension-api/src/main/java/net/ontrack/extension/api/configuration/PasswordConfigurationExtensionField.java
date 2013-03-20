package net.ontrack.extension.api.configuration;

public class PasswordConfigurationExtensionField extends AbstractConfigurationExtensionField {

    public PasswordConfigurationExtensionField(String name, String displayNameKey, String value) {
        super(name, displayNameKey, "password", "", value);
    }
}
