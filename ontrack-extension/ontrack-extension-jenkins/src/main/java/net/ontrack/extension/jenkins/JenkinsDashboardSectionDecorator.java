package net.ontrack.extension.jenkins;

import net.ontrack.core.model.Entity;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.jenkins.client.JenkinsClient;
import net.ontrack.extension.jenkins.client.JenkinsJob;
import net.ontrack.service.DashboardSectionDecorator;
import net.ontrack.service.model.DashboardSectionDecoration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class JenkinsDashboardSectionDecorator implements DashboardSectionDecorator {

    private final PropertiesService propertiesService;
    private final JenkinsClient jenkinsClient;
    private final ExtensionManager extensionManager;

    @Autowired
    public JenkinsDashboardSectionDecorator(PropertiesService propertiesService, JenkinsClient jenkinsClient, ExtensionManager extensionManager) {
        this.propertiesService = propertiesService;
        this.jenkinsClient = jenkinsClient;
        this.extensionManager = extensionManager;
    }

    @Override
    public DashboardSectionDecoration getDecoration(Entity entity, int stampId) {
        if (!extensionManager.isExtensionEnabled(JenkinsExtension.EXTENSION)) {
            return null;
        } else if (entity == Entity.VALIDATION_STAMP) {
            // Gets the Jenkins URL for this validation stamp
            String jobUrl = propertiesService.getPropertyValue(Entity.VALIDATION_STAMP, stampId, JenkinsExtension.EXTENSION, JenkinsUrlPropertyDescriptor.NAME);
            if (StringUtils.isNotBlank(jobUrl)) {
                // Gets the corresponding job
                JenkinsJob job = jenkinsClient.getJob(jobUrl, false);
                // State
                return new DashboardSectionDecoration(
                        Collections.singleton("jenkins-job-" + StringUtils.lowerCase(job.getState().name())),
                        jobUrl
                );
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

}
