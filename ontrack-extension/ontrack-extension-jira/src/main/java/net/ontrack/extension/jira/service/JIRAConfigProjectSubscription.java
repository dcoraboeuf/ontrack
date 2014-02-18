package net.ontrack.extension.jira.service;

import com.google.common.base.Function;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.issue.IssueServiceConfigSubscriber;
import net.ontrack.extension.issue.IssueServiceConfigSubscription;
import net.ontrack.extension.jira.JIRAConfigurationPropertyExtension;
import net.ontrack.extension.jira.JIRAExtension;
import net.ontrack.service.GUIService;
import net.ontrack.service.ManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;

@Component
public class JIRAConfigProjectSubscription implements IssueServiceConfigSubscription {

    private final ManagementService managementService;
    private final PropertiesService propertiesService;
    private final GUIService guiService;

    @Autowired
    public JIRAConfigProjectSubscription(ManagementService managementService, PropertiesService propertiesService, GUIService guiService) {
        this.managementService = managementService;
        this.propertiesService = propertiesService;
        this.guiService = guiService;
    }

    @Override
    public boolean supportsService(String serviceId) {
        return JIRAExtension.EXTENSION.equals(serviceId);
    }

    @Override
    public Collection<? extends IssueServiceConfigSubscriber> getSubscribers(String serviceId, int configId) {
        // Gets the list of projects that have the JIRA property set
        return transform(
                transform(
                        newArrayList(
                                propertiesService.findEntityByPropertyValue(
                                        Entity.PROJECT,
                                        JIRAExtension.EXTENSION,
                                        JIRAConfigurationPropertyExtension.NAME,
                                        String.valueOf(configId)
                                )
                        ),
                        new Function<Integer, ProjectSummary>() {
                            @Override
                            public ProjectSummary apply(Integer projectId) {
                                return managementService.getProject(projectId);
                            }
                        }
                ),
                new Function<ProjectSummary, IssueServiceConfigSubscriber>() {
                    @Override
                    public IssueServiceConfigSubscriber apply(ProjectSummary project) {
                        return new IssueServiceConfigSubscriber(
                                project.getName(),
                                guiService.getProjectGUIURL(project)
                        );
                    }
                }
        );
    }
}
