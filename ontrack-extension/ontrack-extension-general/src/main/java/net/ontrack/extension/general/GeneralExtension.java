package net.ontrack.extension.general;

import net.ontrack.extension.api.decorator.EntityDecorator;
import net.ontrack.extension.api.support.ExtensionAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

/**
 * Common extensions.
 */
@Component
public class GeneralExtension extends ExtensionAdapter {

    public static final String EXTENSION = "general";

    private final ValidationStampWeatherDecorator validationStampWeatherDecorator;

    @Autowired
    public GeneralExtension(ValidationStampWeatherDecorator validationStampWeatherDecorator) {
        super(EXTENSION);
        this.validationStampWeatherDecorator = validationStampWeatherDecorator;
    }

    @Override
    public Collection<? extends EntityDecorator> getDecorators() {
        return Arrays.asList(
                validationStampWeatherDecorator
        );
    }
}
