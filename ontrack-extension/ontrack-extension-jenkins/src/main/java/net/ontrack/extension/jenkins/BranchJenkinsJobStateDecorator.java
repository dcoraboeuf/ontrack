package net.ontrack.extension.jenkins;

import net.ontrack.core.model.Entity;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.jenkins.client.JenkinsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BranchJenkinsJobStateDecorator extends AbstractJenkinsJobStateDecorator {

    @Autowired
    public BranchJenkinsJobStateDecorator(PropertiesService propertiesService, JenkinsClient jenkinsClient, JenkinsDecorator jenkinsDecorator, JenkinsConfigurationExtension configurationExtension) {
        super(propertiesService, jenkinsClient, jenkinsDecorator, Entity.BRANCH, configurationExtension);
    }
}
