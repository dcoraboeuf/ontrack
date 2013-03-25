package net.ontrack.extension.api.configuration;

import net.ontrack.core.validation.ValidationException;
import org.apache.commons.lang3.StringUtils;

public class LongConfigurationExtensionField extends AbstractConfigurationExtensionField {

    private final long min;
    private final long max;

    public LongConfigurationExtensionField(String name, String displayNameKey, long defaultValue, long min, long max, long value) {
        super(name, displayNameKey, "integer", String.valueOf(defaultValue), String.valueOf(value));
        this.min = min;
        this.max = max;
    }

    @Override
    public void validate(String value) {
        if (StringUtils.isNumeric(value)) {
            long n = Long.parseLong(value, 10);
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
