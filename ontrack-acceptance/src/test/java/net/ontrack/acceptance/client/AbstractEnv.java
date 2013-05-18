package net.ontrack.acceptance.client;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import net.ontrack.client.AdminUIClient;
import net.ontrack.client.ControlUIClient;
import net.ontrack.client.ManageUIClient;
import net.ontrack.client.support.*;
import net.ontrack.core.model.*;
import net.ontrack.core.security.SecurityRoles;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;

public abstract class AbstractEnv {

    private final ClientSupport client;

    protected AbstractEnv() {
        String url = System.getProperty("itUrl");
        if (StringUtils.isBlank(url)) {
            // Uses a default URL
            url = "http://localhost:9999/ontrack";
        }
        client = new ClientSupport(url);
    }

    protected ClientSupport getClient() {
        return client;
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

    protected Account doCreateUser() {
        return doCreateUser(
                uid("USR"),
                "Test user",
                "test@test.com",
                SecurityRoles.USER,
                "builtin",
                "test"
        );
    }

    protected Account doCreateUser(final String name, final String fullName, final String email, final String role, final String mode, final String password) {
        return asAdmin(new AdminClientCall<Account>() {
            @Override
            public Account onCall(AdminUIClient ui) {
                // Finds the account with the same name
                Account account = Iterables.find(
                        ui.accounts(),
                        new Predicate<Account>() {
                            @Override
                            public boolean apply(Account a) {
                                return StringUtils.equals(name, a.getName());
                            }
                        },
                        null
                );
                // Deletes it if it exists
                if (account != null) {
                    ui.deleteAccount(account.getId());
                }
                // Creates the account
                ID id = ui.createAccount(new AccountCreationForm(
                        name,
                        fullName,
                        email,
                        role,
                        mode,
                        password,
                        password
                ));
                // Gets the account
                return ui.account(id.getValue());
            }
        });
    }

    protected void assertClientMessage(Runnable task, String pattern, Object... params) {
        try {
            task.run();
        } catch (ClientMessageException ex) {
            assertEquals(format(pattern, params), ex.getMessage());
        }
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

    protected <T> T asAdmin(AdminClientCall<T> call) {
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
