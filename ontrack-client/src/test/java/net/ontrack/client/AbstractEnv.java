package net.ontrack.client;

import net.ontrack.client.support.ClientFactory;
import net.ontrack.core.model.BranchCreationForm;
import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.model.ProjectCreationForm;
import net.ontrack.core.model.ProjectSummary;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

public abstract class AbstractEnv {

    protected final ControlUIClient control;
    protected final ManageUIClient manage;

    protected AbstractEnv() {
        String itPort = System.getProperty("itPort");
        String url = String.format("http://localhost:%s/ontrack", itPort);
        ClientFactory clientFactory = ClientFactory.create(url);
        control = clientFactory.control();
        manage = clientFactory.manage();
    }

    protected BranchSummary createBranch() {
        final ProjectSummary project = createProject();
        return manageAsAdmin(new Callable<BranchSummary>() {
            @Override
            public BranchSummary call() {
                return manage.createBranch(
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
        return manageAsAdmin(new Callable<ProjectSummary>() {
            @Override
            public ProjectSummary call() {
                return manage.createProject(new ProjectCreationForm(
                        uid("PRJ"),
                        "Test project"
                ));
            }
        });
    }

    protected <T> T manageAsAdmin(Callable<T> call) {
        manage.login("admin", "admin");
        try {
            try {
                return call.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } finally {
            manage.logout();
        }
    }

    protected <T> T controlAsAdmin(Callable<T> call) {
        control.login("admin", "admin");
        try {
            try {
                return call.call();
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
