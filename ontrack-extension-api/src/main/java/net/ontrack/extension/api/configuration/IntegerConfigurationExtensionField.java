package net.ontrack.extension.api.configuration;

import net.ontrack.core.validation.ValidationException;
import org.apache.commons.lang3.StringUtils;

public class IntegerConfigurationExtensionField extends AbstractConfigurationExtensionField {

    private final int min;
    private final int max;

    public IntegerConfigurationExtensionField(String name, String displayNameKey, int defaultValue, int min, int max, int value) {
        super(name, displayNameKey, "integer", String.valueOf(defaultValue), String.valueOf(value));
        this.min = min;
        this.max = max;
    }

    @Override
    public void validate(String value) {
        if (StringUtils.isNumeric(value)) {
            int n = Integer.parseInt(value, 10);
            if (n < min) {
                throw new ValidationException("validation.min", value, min);
            } else if (n > max) {
                throw new ValidationException("validation.max", value, max);
            }
        } else {
            throw new ValidationException("validation.notnumeric", value);
        }
    }
}
