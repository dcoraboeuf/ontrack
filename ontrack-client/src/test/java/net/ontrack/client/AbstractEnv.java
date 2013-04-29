package net.ontrack.client;

import net.ontrack.client.support.ClientFactory;
import net.ontrack.core.model.*;

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

    protected BranchSummary doCreateBranch() {
        return doCreateBranch(doCreateProject());
    }

    private BranchSummary doCreateBranch(final ProjectSummary project) {
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

    protected ProjectSummary doCreateProject() {
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

    protected ValidationStampSummary doCreateValidationStamp() {
        return doCreateValidationStamp(doCreateBranch());
    }

    protected ValidationStampSummary doCreateValidationStamp(final BranchSummary branch) {
        return asAdmin(new ManageCall<ValidationStampSummary>() {
            @Override
            public ValidationStampSummary call(ManageUIClient client) {
                return client.createValidationStamp(
                        branch.getProject().getName(),
                        branch.getName(),
                        new ValidationStampCreationForm(
                                uid("STMP"),
                                "Test validation stamp"
                        )
                );
            }
        });
    }

    protected BuildSummary doCreateBuild() {
        return doCreateBuild(doCreateBranch());
    }

    protected BuildSummary doCreateBuild(final BranchSummary branch) {
        return asAdmin(new ControlCall<BuildSummary>() {
            @Override
            public BuildSummary call(ControlUIClient client) {
                return client.createBuild(
                        branch.getProject().getName(),
                        branch.getName(),
                        new BuildCreationForm(
                                uid("BLD"),
                                "Test build",
                                PropertiesCreationForm.create()
                        )
                );
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
