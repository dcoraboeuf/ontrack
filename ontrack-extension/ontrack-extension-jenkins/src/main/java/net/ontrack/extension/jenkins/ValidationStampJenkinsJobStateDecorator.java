package net.ontrack.extension.jenkins;

import net.ontrack.core.model.Decoration;
import net.ontrack.core.model.Entity;
import net.ontrack.extension.api.decorator.EntityDecorator;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.jenkins.client.JenkinsClient;
import net.ontrack.extension.jenkins.client.JenkinsClientException;
import net.sf.jstring.LocalizableMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
public class ValidationStampJenkinsJobStateDecorator implements EntityDecorator {

    private final Logger logger = LoggerFactory.getLogger(ValidationStampJenkinsJobStateDecorator.class);
    private final PropertiesService propertiesService;
    private final JenkinsClient jenkinsClient;

    @Autowired
    public ValidationStampJenkinsJobStateDecorator(PropertiesService propertiesService, JenkinsClient jenkinsClient) {
        this.propertiesService = propertiesService;
        this.jenkinsClient = jenkinsClient;
    }

    @Override
    public EnumSet<Entity> getScope() {
        return EnumSet.of(Entity.VALIDATION_STAMP);
    }

    @Override
    public Decoration getDecoration(Entity entity, int entityId) {
        // Argument check
        Validate.isTrue(entity == Entity.VALIDATION_STAMP, "Expecting validation stamp");
        // Gets the Jenkins URL for this validation stamp
        String jenkinsJobUrl = propertiesService.getPropertyValue(Entity.VALIDATION_STAMP, entityId, JenkinsExtension.EXTENSION, JenkinsUrlPropertyDescriptor.NAME);
        // If no URL is defined, no decoration
        if (StringUtils.isBlank(jenkinsJobUrl)) {
            return null;
        }
        // Gets the state of the job
        try {
            JenkinsJobState jenkinsJobState = getJenkinsJobState(jenkinsJobUrl);
            // Returns a decoration according to the job state
            switch (jenkinsJobState) {
                case DISABLED:
                    return new Decoration(new LocalizableMessage("jenkins.job.disabled")).withIconPath("extension/jenkins-job-disabled.png");
                case RUNNING:
                    return new Decoration(new LocalizableMessage("jenkins.job.running")).withIconPath("extension/jenkins-job-running.png");
                case IDLE:
                    return new Decoration(new LocalizableMessage("jenkins.job.idle")).withIconPath("extension/jenkins-job-idle.png");
                default:
                    return null;
            }
        } catch (JenkinsClientException ex) {
            // Logs an error
            logger.error(String.format("Could not get the job state at %s", jenkinsJobUrl), ex);
            // No decoration
            return null;
        }
    }

    private JenkinsJobState getJenkinsJobState(String jenkinsJobUrl) {
        return jenkinsClient.getJobState(jenkinsJobUrl);
    }
}
