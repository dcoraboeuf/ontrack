package net.ontrack.client;

import net.ontrack.client.support.ClientFactory;
import net.ontrack.core.model.BranchCreationForm;
import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.model.ProjectCreationForm;
import net.ontrack.core.model.ProjectSummary;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class AbstractEnv {

    private final ControlUIClient control;
    private final ManageUIClient manage;

    protected AbstractEnv() {
        String itPort = System.getProperty("itPort");
        String url = String.format("http://localhost:%s/ontrack", itPort);
        ClientFactory clientFactory = ClientFactory.create(url);
        control = clientFactory.control();
        manage = clientFactory.manage();
    }

    protected BranchSummary createBranch() {
        final ProjectSummary project = createProject();
        return asAdmin(new ManageCall<BranchSummary>() {
            @Override
            public BranchSummary call(ManageUIClient client) {
                return client.createBranch(
                        project.getName(),
                        new BranchCreationForm(
                                uid("BRCH"),
                                "Test branch"
                        )
                );
            }
        });
    }

    protected ProjectSummary createProject() {
        return asAdmin(new ManageCall<ProjectSummary>() {
            @Override
            public ProjectSummary call(ManageUIClient client) {
                return client.createProject(new ProjectCreationForm(
                        uid("PRJ"),
                        "Test project"
                ));
            }
        });
    }

    protected <T> T asAdmin(ManageCall<T> call) {
        manage.login("admin", "admin");
        try {
            try {
                return call.call(manage);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } finally {
            manage.logout();
        }
    }

    protected <T> T asAdmin(ControlCall<T> call) {
        control.login("admin", "admin");
        try {
            try {
                return call.call(control);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } finally {
            control.logout();
        }
    }

    protected String uid(String prefix) {
        return prefix + new SimpleDateFormat("mmssSSS").format(new Date());
    }


}
