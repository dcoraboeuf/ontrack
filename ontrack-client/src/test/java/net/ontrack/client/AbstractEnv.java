package net.ontrack.client;

import net.ontrack.client.support.ClientFactory;
import net.ontrack.core.model.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
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

    protected PromotionLevelSummary doCreatePromotionLevel() {
        return doCreatePromotionLevel(doCreateBranch());
    }

    private PromotionLevelSummary doCreatePromotionLevel(final BranchSummary branch) {
        return asAdmin(new ManageCall<PromotionLevelSummary>() {
            @Override
            public PromotionLevelSummary call(ManageUIClient client) {
                return client.createPromotionLevel(
                        branch.getProject().getName(),
                        branch.getName(),
                        new PromotionLevelCreationForm(
                                uid("PL"),
                                "Test promotion level"
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

    protected <T> T anonymous(ManageCall<T> call) {
        try {
            return call.call(manage);
        } catch (Exception e) {
            throw new RuntimeException(e);
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

    protected MultipartFile mockImage(String path) {
        try (InputStream in = getClass().getResourceAsStream(path)) {
            byte[] content = IOUtils.toByteArray(in);
            return new MockMultipartFile(
                    StringUtils.substringAfterLast(path, "/"),
                    path,
                    "image/png",
                    content
            );
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected String uid(String prefix) {
        return prefix + new SimpleDateFormat("mmssSSS").format(new Date());
    }


}
