package net.ontrack.backend;

import net.ontrack.core.model.Entity;
import net.ontrack.core.model.PropertiesCreationForm;
import net.ontrack.core.model.PropertyCreationForm;
import net.ontrack.core.model.PropertyValue;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.test.AbstractIntegrationTest;
import net.sf.jstring.Strings;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PropertiesServiceTest extends AbstractIntegrationTest {

    @Autowired
    private Strings strings;

    @Autowired
    private PropertiesService propertiesService;

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
        String html = propertiesService.toHTML(strings, Locale.ENGLISH, "jenkins", "url", "http://jenkins/test/2");
        // Check
        assertEquals("<span title=\"Jenkins URL\"><img src=\"extension/jenkins.png\" /> <a href=\"http://jenkins/test/2\">http://jenkins/test/2</a></span>", html);
    }

}
