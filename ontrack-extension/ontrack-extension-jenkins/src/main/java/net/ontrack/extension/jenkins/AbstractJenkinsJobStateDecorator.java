package net.ontrack.extension.jenkins;

import net.ontrack.core.model.Decoration;
import net.ontrack.core.model.Entity;
import net.ontrack.extension.api.decorator.EntityDecorator;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.jenkins.client.JenkinsClient;
import net.ontrack.extension.jenkins.client.JenkinsClientException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;

public abstract class AbstractJenkinsJobStateDecorator implements EntityDecorator {

    private final Logger logger = LoggerFactory.getLogger(AbstractJenkinsJobStateDecorator.class);
    private final PropertiesService propertiesService;
    private final JenkinsClient jenkinsClient;
    private final JenkinsDecorator jenkinsDecorator;
    private final Entity targetEntity;

    public AbstractJenkinsJobStateDecorator(PropertiesService propertiesService, JenkinsClient jenkinsClient, JenkinsDecorator jenkinsDecorator, Entity targetEntity) {
        this.propertiesService = propertiesService;
        this.jenkinsClient = jenkinsClient;
        this.jenkinsDecorator = jenkinsDecorator;
        this.targetEntity = targetEntity;
    }

    @Override
    public EnumSet<Entity> getScope() {
        return EnumSet.of(targetEntity);
    }

    @Override
    public Decoration getDecoration(Entity entity, int entityId) {
        // Argument check
        Validate.isTrue(entity == targetEntity, "Expecting entity " + targetEntity);
        // Gets the Jenkins URL for this branch
        String jenkinsJobUrl = propertiesService.getPropertyValue(targetEntity, entityId, JenkinsExtension.EXTENSION, JenkinsUrlPropertyDescriptor.NAME);
        // If no URL is defined, no decoration
        if (StringUtils.isBlank(jenkinsJobUrl)) {
            return null;
        }
        // Gets the state of the job
        try {
            JenkinsJobState jenkinsJobState = getJenkinsJobState(jenkinsJobUrl);
            // Returns a decoration according to the job state
            return jenkinsDecorator.getJobDecoration(jenkinsJobState);
        } catch (JenkinsClientException ex) {
            // Logs an error
            logger.error(String.format("Could not get the job state at %s", jenkinsJobUrl), ex);
            // No decoration
            return null;
        }
    }

    private JenkinsJobState getJenkinsJobState(String jenkinsJobUrl) {
        return jenkinsClient.getJob(jenkinsJobUrl).getState();
    }
}
