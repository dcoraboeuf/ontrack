package net.ontrack.extension.general;

import net.ontrack.core.model.Decoration;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.Status;
import net.ontrack.core.model.ValidationRunStatusStub;
import net.ontrack.service.ManagementService;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValidationStampWeatherDecoratorTest {

    private ManagementService managementService;
    private ValidationStampWeatherDecorator decorator;

    @Before
    public void before() {
        managementService = mock(ManagementService.class);
        decorator = new ValidationStampWeatherDecorator(managementService);
    }

    @Test
    public void no_run_is_sunny() {
        when(managementService.getStatusesForLastBuilds(1, 5)).thenReturn(Collections.<ValidationRunStatusStub>emptyList());
        Decoration decoration = decorator.getDecoration(Entity.VALIDATION_STAMP, 1);
        assertNotNull("Decoration is not null", decoration);
        assertSunny(decoration);
    }

    @Test
    public void only_passed_is_sunny() {
        for (int i = 1; i <= 5; i++) {
            when(managementService.getStatusesForLastBuilds(i, 5)).thenReturn(Collections.nCopies(
                    i,
                    new ValidationRunStatusStub(
                            10,
                            Status.PASSED,
                            ""
                    )
            ));
            Decoration decoration = decorator.getDecoration(Entity.VALIDATION_STAMP, i);
            assertNotNull("Decoration is not null", decoration);
            assertSunny(decoration);
        }
    }

    @Test
    public void only_warning_is_sunny() {
        for (int i = 1; i <= 5; i++) {
            when(managementService.getStatusesForLastBuilds(i, 5)).thenReturn(Collections.nCopies(
                    i,
                    new ValidationRunStatusStub(
                            10,
                            Status.WARNING,
                            ""
                    )
            ));
            Decoration decoration = decorator.getDecoration(Entity.VALIDATION_STAMP, i);
            assertNotNull("Decoration is not null", decoration);
            assertSunny(decoration);
        }
    }

    @Test
    public void mixed_warning_and_success_is_sunny() {
        when(managementService.getStatusesForLastBuilds(1, 5)).thenReturn(Arrays.asList(
                new ValidationRunStatusStub(1, Status.PASSED, ""),
                new ValidationRunStatusStub(1, Status.WARNING, ""),
                new ValidationRunStatusStub(1, Status.PASSED, "")
        ));
        Decoration decoration = decorator.getDecoration(Entity.VALIDATION_STAMP, 1);
        assertNotNull("Decoration is not null", decoration);
        assertSunny(decoration);
    }

    @Test
    public void failed_warning_passed_is_sunny_with_clouds() {
        when(managementService.getStatusesForLastBuilds(1, 5)).thenReturn(Arrays.asList(
                new ValidationRunStatusStub(1, Status.FAILED, ""),
                new ValidationRunStatusStub(2, Status.WARNING, ""),
                new ValidationRunStatusStub(3, Status.PASSED, "")
        ));
        Decoration decoration = decorator.getDecoration(Entity.VALIDATION_STAMP, 1);
        assertNotNull("Decoration is not null", decoration);
        assertSunnyAndClouds(decoration);
    }

    @Test
    public void failed_warning_warning_warning_passed_is_sunny_with_clouds() {
        when(managementService.getStatusesForLastBuilds(1, 5)).thenReturn(Arrays.asList(
                new ValidationRunStatusStub(1, Status.FAILED, ""),
                new ValidationRunStatusStub(2, Status.WARNING, ""),
                new ValidationRunStatusStub(3, Status.WARNING, ""),
                new ValidationRunStatusStub(4, Status.WARNING, ""),
                new ValidationRunStatusStub(5, Status.PASSED, "")
        ));
        Decoration decoration = decorator.getDecoration(Entity.VALIDATION_STAMP, 1);
        assertNotNull("Decoration is not null", decoration);
        assertSunnyAndClouds(decoration);
    }

    private void assertSunny(Decoration decoration) {
        assertWeather(decoration, "Sunny is expected", "sunny");
    }

    private void assertSunnyAndClouds(Decoration decoration) {
        assertWeather(decoration, "Sunny with clouds is expected", "sunAndClouds");
    }

    private void assertWeather(Decoration decoration, String message, String icon) {
        assertEquals(message, format("extension/weather/%s.png", icon), decoration.getIconPath());
    }

}
