package net.ontrack.extension.general;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import net.ontrack.core.model.Decoration;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.Status;
import net.ontrack.core.model.ValidationRunStatusStub;
import net.ontrack.extension.api.decorator.EntityDecorator;
import net.ontrack.service.ManagementService;
import net.sf.jstring.LocalizableMessage;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

/**
 * Associates a "weather" icon to the status of a validation stamp.
 * <p/>
 * A validation stamp status is computed by looking at the last validation run status
 * for the last 5 builds. A status is considered OK only is {@link net.ontrack.core.model.Status#PASSED PASSED}
 * or {@link net.ontrack.core.model.Status#WARNING WARNING}.
 * <p/>
 * We then count the number of statuses which are not OK:
 * <ul>
 * <li>0 - sunny</li>
 * <li>1 - sun and clouds</li>
 * <li>2 - clouds</li>
 * <li>3 - rain</li>
 * <li>4 - storm</li>
 * </ul>
 */
@Component
public class ValidationStampWeatherDecorator implements EntityDecorator {

    public static final String EXTENSION_GENERAL_WEATHER_SUNNY = "extension.general.weather.sunny";
    public static final String EXTENSION_GENERAL_WEATHER_SUN_AND_CLOUDS = "extension.general.weather.sunAndClouds";
    public static final String EXTENSION_GENERAL_WEATHER_CLOUDS = "extension.general.weather.clouds";
    public static final String EXTENSION_GENERAL_WEATHER_RAIN = "extension.general.weather.rain";
    public static final String EXTENSION_GENERAL_WEATHER_STORM = "extension.general.weather.storm";
    private final ManagementService managementService;

    @Autowired
    public ValidationStampWeatherDecorator(ManagementService managementService) {
        this.managementService = managementService;
    }

    @Override
    public EnumSet<Entity> getScope() {
        return EnumSet.of(Entity.VALIDATION_STAMP);
    }

    @Override
    public Decoration getDecoration(Entity entity, int validationStampId) {
        // Argument check
        Validate.isTrue(entity == Entity.VALIDATION_STAMP, "Expecting validation stamp");
        // List of statuses for the last 5 builds
        List<ValidationRunStatusStub> statuses = managementService.getStatusesForLastBuilds(validationStampId, 5);
        // Keeps only the ones which are NOT PASSED and NOT WARNING
        Collection<ValidationRunStatusStub> invalidStatuses = Collections2.filter(
                statuses,
                new Predicate<ValidationRunStatusStub>() {
                    @Override
                    public boolean apply(ValidationRunStatusStub stub) {
                        return stub.getStatus() != Status.PASSED && stub.getStatus() != Status.WARNING;
                    }
                }
        );
        // Result
        switch (invalidStatuses.size()) {
            case 0:
                return sunny();
            case 1:
                return sunAndClouds();
            case 2:
                return clouds();
            case 3:
                return rain();
            case 4:
            default:
                return storm();
        }
    }

    private Decoration sunny() {
        return weather(EXTENSION_GENERAL_WEATHER_SUNNY, "sunny");
    }

    private Decoration sunAndClouds() {
        return weather(EXTENSION_GENERAL_WEATHER_SUN_AND_CLOUDS, "sunAndClouds");
    }

    private Decoration clouds() {
        return weather(EXTENSION_GENERAL_WEATHER_CLOUDS, "clouds");
    }

    private Decoration rain() {
        return weather(EXTENSION_GENERAL_WEATHER_RAIN, "rain");
    }

    private Decoration storm() {
        return weather(EXTENSION_GENERAL_WEATHER_STORM, "storm");
    }

    private Decoration weather(String key, String icon) {
        return new Decoration(new LocalizableMessage(key))
                .withIconPath(String.format("extension/weather/%s.png", icon));
    }
}
