package net.ontrack.extension.jenkins;

import net.ontrack.core.model.DashboardSection;
import net.ontrack.core.model.Entity;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.jenkins.client.JenkinsClient;
import net.ontrack.extension.jenkins.client.JenkinsJob;
import net.ontrack.service.DashboardSectionProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JenkinsDashboardSectionProvider implements DashboardSectionProvider {

    private final PropertiesService propertiesService;
    private final JenkinsClient jenkinsClient;

    @Autowired
    public JenkinsDashboardSectionProvider(PropertiesService propertiesService, JenkinsClient jenkinsClient) {
        this.propertiesService = propertiesService;
        this.jenkinsClient = jenkinsClient;
    }

    @Override
    public boolean apply(Entity entity, int entityId) {
        return entity == Entity.BRANCH;
    }

    @Override
    public DashboardSection getSection(Entity entity, int branchId) {
        if (entity == Entity.BRANCH) {
            // Gets the Jenkins URL for this branch
            String jenkinsJobUrl = propertiesService.getPropertyValue(Entity.BRANCH, branchId, JenkinsExtension.EXTENSION, JenkinsUrlPropertyDescriptor.NAME);
            if (StringUtils.isBlank(jenkinsJobUrl)) {
                return null;
            }
            // Gets the job
            JenkinsJob job = jenkinsClient.getJob(jenkinsJobUrl, true);
            // OK
            return new DashboardSection(
                    "extension/jenkins-dashboard-section",
                    job
            );
        } else {
            return null;
        }
    }

}
