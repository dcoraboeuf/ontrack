package net.ontrack.backend.config;

import net.ontrack.backend.AbstractBackendTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.regex.Pattern;

public class EnvInfoServiceTest extends AbstractBackendTest {

    @Autowired
    private EnvInfoService envInfoService;

    @Test
    public void version() {
        String version = envInfoService.getVersion();
        Assert.assertTrue(
                "Version should be like 1.x but is " + version,
                Pattern.matches(
                        "1\\.\\d+(\\.\\d+)?(-SNAPSHOT)?",
                        version
                )
        );
    }

}
