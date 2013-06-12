package net.ontrack.extension.jenkins;

import net.ontrack.core.model.Entity;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.jenkins.client.JenkinsClient;
import net.ontrack.extension.jenkins.client.JenkinsJob;
import net.ontrack.service.DashboardSectionDecorator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component
public class JenkinsDashboardSectionDecorator implements DashboardSectionDecorator {

    private final PropertiesService propertiesService;
    private final JenkinsClient jenkinsClient;
    private final JenkinsConfigurationExtension jenkinsConfiguration;

    @Autowired
    public JenkinsDashboardSectionDecorator(PropertiesService propertiesService, JenkinsClient jenkinsClient, JenkinsConfigurationExtension jenkinsConfiguration) {
        this.propertiesService = propertiesService;
        this.jenkinsClient = jenkinsClient;
        this.jenkinsConfiguration = jenkinsConfiguration;
    }

    @Override
    public Collection<String> getClasses(Entity entity, int stampId) {
        if (entity == Entity.VALIDATION_STAMP) {
            // Gets the Jenkins URL for this validation stamp
            String jobUrl = propertiesService.getPropertyValue(Entity.VALIDATION_STAMP, stampId, JenkinsExtension.EXTENSION, JenkinsUrlPropertyDescriptor.NAME);
            if (StringUtils.isNotBlank(jobUrl)) {
                // Gets the corresponding job
                JenkinsJob job = jenkinsClient.getJob(jenkinsConfiguration, jobUrl, false);
                // State
                return Collections.singleton("jenkins-job-" + StringUtils.lowerCase(job.getState().name()));
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
