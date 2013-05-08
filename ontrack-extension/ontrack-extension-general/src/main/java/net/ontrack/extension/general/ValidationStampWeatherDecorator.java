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
 * for the last 5 builds. A status is considered OK only is {@link net.ontrack.core.model.Status#PASSED PASSED}.
 *
 * We then count the number of statuses which are not OK:
 * <ul>
 *     <li>0 - sunny</li>
 *     <li>1 - sun and clouds</li>
 *     <li>2 - clouds</li>
 *     <li>3 - rain</li>
 *     <li>4 - storm</li>
 * </ul>
 */
@Component
public class ValidationStampWeatherDecorator implements EntityDecorator {

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
        // Keeps only the ones which are NOT PASSED
        Collection<ValidationRunStatusStub> invalidStatuses = Collections2.filter(
                statuses,
                new Predicate<ValidationRunStatusStub>() {
                    @Override
                    public boolean apply(ValidationRunStatusStub stub) {
                        return stub.getStatus() != Status.PASSED;
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
        return weather("extension.general.weather.sunny", "sunny");
    }

    private Decoration sunAndClouds() {
        return weather("extension.general.weather.sunAndClouds", "sunAndClouds");
    }

    private Decoration clouds() {
        return weather("extension.general.weather.clouds", "clouds");
    }

    private Decoration rain() {
        return weather("extension.general.weather.rain", "rain");
    }

    private Decoration storm() {
        return weather("extension.general.weather.storm", "storm");
    }

    private Decoration weather(String key, String icon) {
        return new Decoration(new LocalizableMessage(key))
                .withIconPath(String.format("extension/weather/%s.png", icon));
    }
}
