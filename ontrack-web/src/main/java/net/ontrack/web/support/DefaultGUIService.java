package net.ontrack.web.support;

import net.ontrack.core.model.BuildSummary;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.service.AdminService;
import net.ontrack.service.GUIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultGUIService implements GUIService {

    private final AdminService adminService;

    @Autowired
    public DefaultGUIService(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public String toGUI(String uri) {
        return toURL("gui/" + uri);
    }

    @Override
    public String getBuildGUIURL(BuildSummary build) {
        return toGUI(getBuildURI(build));
    }

    @Override
    public String getBuildURI(BuildSummary build) {
        return String.format(
                "project/%s/branch/%s/build/%s",
                build.getBranch().getProject().getName(),
                build.getBranch().getName(),
                build.getName());
    }

    @Override
    public String getProjectGUIURL(ProjectSummary project) {
        return toGUI(getProjectURI(project));
    }

    @Override
    public String getProjectURI(ProjectSummary project) {
        return String.format(
                "project/%s",
                project.getName());
    }

    private String toURL(String uri) {
        return getBaseURL() + uri;
    }

    /**
     * Since this method can be called from a batch, the base URL
     * cannot be deducted from a HTTP request. It must therefore
     * be a configuration parameter.
     */
    public String getBaseURL() {
        return adminService.getGeneralConfiguration().getBaseUrl();
    }
}
