package net.ontrack.backend;

import net.ontrack.core.model.Entity;
import net.ontrack.core.model.PropertiesCreationForm;
import net.ontrack.core.model.PropertyCreationForm;
import net.ontrack.service.PropertiesService;
import net.ontrack.test.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PropertiesServiceTest extends AbstractIntegrationTest {

    @Autowired
    private PropertiesService propertiesService;

    @Test
    public void createProperties() {
        propertiesService.createProperties(Entity.BUILD, 1, PropertiesCreationForm.create().with(
                new PropertyCreationForm("jenkins", "url", "http://jenkins/test/1")
        ));
    }

}
