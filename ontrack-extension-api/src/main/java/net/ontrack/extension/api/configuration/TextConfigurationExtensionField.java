package net.ontrack.extension.api.configuration;

public class TextConfigurationExtensionField extends AbstractConfigurationExtensionField {

    public TextConfigurationExtensionField(String name, String displayNameKey, String value) {
        super(name, displayNameKey, "text", "", value);
    }
}
