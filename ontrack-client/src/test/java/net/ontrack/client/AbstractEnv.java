package net.ontrack.client;

import net.ontrack.client.support.ClientSupport;
import net.ontrack.client.support.ControlClientCall;
import net.ontrack.client.support.ManageClientCall;
import net.ontrack.client.support.PropertyClientCall;
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

    private final ClientSupport client;

    protected AbstractEnv() {
        String itPort = System.getProperty("itPort");
        String url = String.format("http://localhost:%s/ontrack", itPort);
        client = new ClientSupport(url);
    }

    protected BranchSummary doCreateBranch() {
        return doCreateBranch(doCreateProject());
    }

    private BranchSummary doCreateBranch(final ProjectSummary project) {
        return asAdmin(new ManageClientCall<BranchSummary>() {
            @Override
            public BranchSummary onCall(ManageUIClient client) {
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
        return asAdmin(new ManageClientCall<ProjectSummary>() {
            @Override
            public ProjectSummary onCall(ManageUIClient client) {
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
        return asAdmin(new ManageClientCall<ValidationStampSummary>() {
            @Override
            public ValidationStampSummary onCall(ManageUIClient client) {
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
        return asAdmin(new ManageClientCall<PromotionLevelSummary>() {
            @Override
            public PromotionLevelSummary onCall(ManageUIClient client) {
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
        return asAdmin(new ControlClientCall<BuildSummary>() {
            @Override
            public BuildSummary onCall(ControlUIClient client) {
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

    protected <T> T asAdmin(ManageClientCall<T> call) {
        return client.asUser("admin", "admin", call);
    }

    protected <T> T anonymous(ManageClientCall<T> call) {
        return client.anonymous(call);
    }

    protected <T> T asAdmin(ControlClientCall<T> call) {
        return client.asUser("admin", "admin", call);
    }

    protected <T> T asAdmin(PropertyClientCall<T> call) {
        return client.asUser("admin", "admin", call);
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
