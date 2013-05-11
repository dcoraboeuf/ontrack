package net.ontrack.extension.jenkins;

import net.ontrack.core.model.Entity;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.jenkins.client.JenkinsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidationStampJenkinsJobStateDecorator extends AbstractJenkinsJobStateDecorator {

    @Autowired
    public ValidationStampJenkinsJobStateDecorator(PropertiesService propertiesService, JenkinsClient jenkinsClient, JenkinsDecorator jenkinsDecorator) {
        super(propertiesService, jenkinsClient, jenkinsDecorator, Entity.VALIDATION_STAMP);
    }
}
