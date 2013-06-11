package net.ontrack.extension.jenkins;

import net.ontrack.core.model.DashboardStatus;
import net.ontrack.core.model.Entity;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.jenkins.client.JenkinsClient;
import net.ontrack.extension.jenkins.client.JenkinsJob;
import net.ontrack.service.DashboardStatusProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JenkinsDashboardStatusProvider implements DashboardStatusProvider {

    private final PropertiesService propertiesService;
    private final JenkinsClient jenkinsClient;

    @Autowired
    public JenkinsDashboardStatusProvider(PropertiesService propertiesService, JenkinsClient jenkinsClient) {
        this.propertiesService = propertiesService;
        this.jenkinsClient = jenkinsClient;
    }

    @Override
    public boolean apply(Entity entity, int entityId) {
        return entity == Entity.BRANCH;
    }

    @Override
    public DashboardStatus getStatus(Entity entity, int branchId) {
        if (entity == Entity.BRANCH) {
            // Gets the Jenkins URL for this branch
            String jenkinsJobUrl = propertiesService.getPropertyValue(Entity.BRANCH, branchId, JenkinsExtension.EXTENSION, JenkinsUrlPropertyDescriptor.NAME);
            if (StringUtils.isBlank(jenkinsJobUrl)) {
                return null;
            }
            // Initial status
            DashboardStatus status = new DashboardStatus();
            // Gets the job
            JenkinsJob job = jenkinsClient.getJob(jenkinsJobUrl);
            // Gets the state of this job
            JenkinsJobState jobState = job.getState();
            switch (jobState) {
                case RUNNING:
                    status = status.withIcon("extension/jenkins-job-running.png");
                    break;
                case DISABLED:
                    status = status.addCss("jenkins-job-disabled");
                    break;
                case IDLE:
                default:
                    // Nothing
                    break;
            }
            // Gets the last result for this job
            JenkinsJobResult jobResult = job.getResult();
            status = status.addCss("jenkins-result-" + StringUtils.lowerCase(jobResult.name()));
            // OK
            return status;
        } else {
            return null;
        }
    }

}
