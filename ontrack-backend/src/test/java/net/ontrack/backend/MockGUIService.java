package net.ontrack.backend;

import net.ontrack.core.RunProfile;
import net.ontrack.core.model.BuildSummary;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.service.GUIService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(RunProfile.TEST)
public class MockGUIService implements GUIService {
    @Override
    public String toGUI(String uri) {
        return "http://test/gui/" + uri;
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
}
