package net.ontrack.backend;

import net.ontrack.core.model.Entity;
import net.ontrack.core.model.PropertiesCreationForm;
import net.ontrack.core.model.PropertyCreationForm;
import net.ontrack.core.model.PropertyValue;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.api.property.PropertiesService;
import net.sf.jstring.Strings;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PropertiesServiceTest extends AbstractBackendTest {

    @Autowired
    private Strings strings;
    @Autowired
    private PropertiesService propertiesService;
    @Autowired
    private ExtensionManager extensionManager;
    @Autowired
    private SecurityUtils securityUtils;

    @Before
    public void init() {
        // Makes sure the Jenkins extension is enabled
        securityUtils.asAdmin(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                extensionManager.enableExtension("jenkins");
                return null;
            }
        });
    }

    @Test
    public void createProperties() {
        // Creates the properties
        propertiesService.createProperties(Entity.BUILD, 1, PropertiesCreationForm.create().with(
                new PropertyCreationForm("jenkins", "url", "http://jenkins/test/1")
        ));
        // Gets the list of properties
        List<PropertyValue> values = propertiesService.getPropertyValues(Entity.BUILD, 1);
        assertTrue(values.contains(new PropertyValue("jenkins", "url", "http://jenkins/test/1")));
        // Asserts the value is there
        assertEquals(
                "http://jenkins/test/1",
                propertiesService.getPropertyValue(Entity.BUILD, 1, "jenkins", "url"));
    }

    @Test
    public void rendering() {
        // Rendering
        String html = propertiesService.toHTML(strings, Locale.ENGLISH, "jenkins", "url", Entity.PROJECT, 1, "http://jenkins/test/2");
        // Check
        assertEquals("<a href=\"http://jenkins/test/2\">http://jenkins/test/2</a>", html);
    }

}
