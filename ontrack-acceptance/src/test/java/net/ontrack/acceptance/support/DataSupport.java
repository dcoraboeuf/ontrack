package net.ontrack.acceptance.support;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import net.ontrack.client.AdminUIClient;
import net.ontrack.client.ControlUIClient;
import net.ontrack.client.ManageUIClient;
import net.ontrack.client.support.*;
import net.ontrack.core.model.*;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.core.support.NotFoundException;
import net.thucydides.core.annotations.Step;
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

public class DataSupport {

    private final ClientSupport client;

    public DataSupport() {
        // Base URL
        String url = System.getProperty("webdriver.base.url");
        if (StringUtils.isBlank(url)) {
            throw new IllegalStateException("No default URL defined at 'webdriver.base.url'");
        }
        // Client support
        client = new ClientSupport(url);
    }

    public ClientSupport getClient() {
        return client;
    }

    public String getAdminPassword() {
        String pwd = System.getProperty("itAdminPassword");
        if (StringUtils.isNotBlank(pwd)) {
            return pwd;
        } else {
            return "admin";
        }
    }

    @Step
    public void create_project(final String project, final String description) {
        asAdmin(new ManageClientCall<Void>() {
            @Override
            public Void onCall(ManageUIClient ui) {
                ui.createProject(new ProjectCreationForm(
                        project,
                        description
                ));
                return null;
            }
        });
    }

    @Step
    public void ensure_project_exists(final String project) {
        asAdmin(new ManageClientCall<Void>() {
            @Override
            public Void onCall(ManageUIClient ui) {
                try {
                    ui.getProject(project);
                    // Already exists
                } catch (NotFoundException ex) {
                    // Creates it
                    create_project(project, "Description for " + project);
                }
                return null;
            }
        });
    }

    @Step
    public void delete_project(final String project) {
        asAdmin(new ManageClientCall<Void>() {
            @Override
            public Void onCall(ManageUIClient ui) {
                ProjectSummary summary = ui.getProject(project);
                if (summary != null) {
                    ui.deleteProject(project);
                }
                return null;
            }
        });
    }

    @Step
    public void create_branch(final String project, final String branch, final String description) {
        asAdmin(new ManageClientCall<Void>() {
            @Override
            public Void onCall(ManageUIClient ui) {
                ui.createBranch(
                        project,
                        new BranchCreationForm(
                                branch,
                                description
                        ));
                return null;
            }
        });
    }

    @Step
    public void ensure_branch_exists(final String project, final String branch) {
        asAdmin(new ManageClientCall<Void>() {
            @Override
            public Void onCall(ManageUIClient ui) {
                try {
                    ui.getBranch(project, branch);
                    // Already exists
                } catch (NotFoundException ex) {
                    // Creates it
                    create_branch(project, branch, "Description for " + branch);
                }
                return null;
            }
        });
    }

    @Step
    public void create_promotion_level(final String project, final String branch, final String promotionLevel, final String description) {
        asAdmin(new ManageClientCall<Void>() {
            @Override
            public Void onCall(ManageUIClient ui) {
                ui.createPromotionLevel(
                        project,
                        branch,
                        new PromotionLevelCreationForm(
                                promotionLevel,
                                description
                        )
                );
                return null;
            }
        });
    }

    @Step
    public void delete_promotion_level(final String project, final String branch, final String promotionLevel) {
        asAdmin(new ManageClientCall<Void>() {
            @Override
            public Void onCall(ManageUIClient ui) {
                PromotionLevelSummary summary = ui.getPromotionLevel(project, branch, promotionLevel);
                if (summary != null) {
                    ui.deletePromotionLevel(project, branch, promotionLevel);
                }
                return null;
            }
        });
    }

    @Step
    public void create_validation_stamp(final String project, final String branch, final String validationStamp, final String description) {
        asAdmin(new ManageClientCall<Void>() {
            @Override
            public Void onCall(ManageUIClient ui) {
                ui.createValidationStamp(
                        project,
                        branch,
                        new ValidationStampCreationForm(
                                validationStamp,
                                description
                        )
                );
                return null;
            }
        });
    }

    @Step
    public void delete_validation_stamp(final String project, final String branch, final String validationStamp) {
        asAdmin(new ManageClientCall<Void>() {
            @Override
            public Void onCall(ManageUIClient ui) {
                ValidationStampSummary summary = ui.getValidationStamp(project, branch, validationStamp);
                if (summary != null) {
                    ui.deleteValidationStamp(project, branch, validationStamp);
                }
                return null;
            }
        });
    }

    @Step
    public void associate_validation_stamp_with_promotion_level(final String project, final String branch, final String validationStamp, final String promotionLevel) {
        asAdmin(new ManageClientCall<Void>() {
            @Override
            public Void onCall(ManageUIClient ui) {
                ui.linkValidationStampToPromotionLevel(project, branch, validationStamp, promotionLevel);
                return null;
            }
        });
    }

    @Step
    public void define_user(final String name, final String fullName, final String password) {
        doCreateUser(
                name,
                fullName,
                name + "@test.com",
                SecurityRoles.USER,
                "builtin",
                password
        );
    }

    @Step
    public BranchSummary doCreateBranch() {
        return doCreateBranch(doCreateProject());
    }

    @Step
    public BranchSummary doCreateBranch(final ProjectSummary project) {
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

    @Step
    public ProjectSummary doCreateProject() {
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

    @Step
    public ValidationStampSummary doCreateValidationStamp() {
        return doCreateValidationStamp(doCreateBranch());
    }

    @Step
    public ValidationStampSummary doCreateValidationStamp(final BranchSummary branch) {
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

    @Step
    public PromotionLevelSummary doCreatePromotionLevel() {
        return doCreatePromotionLevel(doCreateBranch());
    }

    @Step
    public PromotionLevelSummary doCreatePromotionLevel(final BranchSummary branch) {
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

    @Step
    public BuildSummary doCreateBuild() {
        return doCreateBuild(doCreateBranch());
    }

    @Step
    public BuildSummary doCreateBuild(final BranchSummary branch) {
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

    @Step
    public Account doCreateUser() {
        return doCreateUser(
                uid("USR"),
                "Test user",
                "test@test.com",
                SecurityRoles.USER,
                "builtin",
                "test"
        );
    }

    @Step
    public Account doCreateUser(final String name, final String fullName, final String email, final String role, final String mode, final String password) {
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

    public void assertClientMessage(Runnable task, String pattern, Object... params) {
        try {
            task.run();
        } catch (ClientMessageException ex) {
            assertEquals(format(pattern, params), ex.getMessage());
        }
    }

    public <T> T asAdmin(ManageClientCall<T> call) {
        return client.asUser("admin", getAdminPassword(), call);
    }

    public <T> T anonymous(ManageClientCall<T> call) {
        return client.anonymous(call);
    }

    public <T> T asAdmin(ControlClientCall<T> call) {
        return client.asUser("admin", getAdminPassword(), call);
    }

    public <T> T asAdmin(PropertyClientCall<T> call) {
        return client.asUser("admin", getAdminPassword(), call);
    }

    public <T> T asAdmin(AdminClientCall<T> call) {
        return client.asUser("admin", getAdminPassword(), call);
    }

    public MultipartFile mockImage(String path) {
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

    public String uid(String prefix) {
        return prefix + new SimpleDateFormat("mmssSSS").format(new Date());
    }
}
