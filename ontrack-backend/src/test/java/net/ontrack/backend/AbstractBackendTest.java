package net.ontrack.backend;

import net.ontrack.backend.config.BackendSecurityConfig;
import net.ontrack.backend.security.AccountAuthentication;
import net.ontrack.core.model.*;
import net.ontrack.core.security.GlobalFunction;
import net.ontrack.core.security.ProjectFunction;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.service.AccountService;
import net.ontrack.service.ControlService;
import net.ontrack.service.ManagementService;
import net.ontrack.test.AbstractIntegrationTest;
import net.ontrack.test.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.ContextConfiguration;

import java.util.Locale;
import java.util.concurrent.Callable;

@ContextConfiguration(classes = BackendSecurityConfig.class)
public abstract class AbstractBackendTest extends AbstractIntegrationTest {

    @Autowired
    private ManagementService managementService;
    @Autowired
    private ControlService controlService;
    @Autowired
    private AccountService accountService;

    protected AnonymousCall asAnonymous() {
        return new AnonymousCall();
    }

    protected UserCall asUser() {
        return new UserCall();
    }

    protected AccountCall asAccount(Account account) {
        return new AccountCall(account);
    }

    protected AdminCall asAdmin() {
        return new AdminCall();
    }

    protected String uid(String prefix) {
        return Helper.uid(prefix);
    }

    protected Account doCreateAccount() throws Exception {
        return doCreateAccount(uid("ACC"));
    }

    protected Account doCreateAccount(final String name) throws Exception {
        return asAdmin().call(new Callable<Account>() {
            @Override
            public Account call() throws Exception {
                return accountService.getAccount(
                        accountService.createAccount(
                                new AccountCreationForm(
                                        name,
                                        name + " Test",
                                        name + "@test.com",
                                        SecurityRoles.USER,
                                        "builtin",
                                        "test",
                                        "test"
                                )
                        ).getValue()
                );
            }
        });
    }

    protected ProjectSummary doCreateProject() throws Exception {
        final String projectName = uid("PRJ");
        return asAdmin().call(new Callable<ProjectSummary>() {
            @Override
            public ProjectSummary call() throws Exception {
                return managementService.createProject(new ProjectCreationForm(projectName, "My description"));
            }
        });
    }

    protected BranchSummary doCreateBranch(final int projectId) throws Exception {
        final String branchName = uid("BCH");
        return asAdmin().call(new Callable<BranchSummary>() {
            @Override
            public BranchSummary call() throws Exception {
                return managementService.createBranch(projectId, new BranchCreationForm(branchName, "Branch description"));
            }
        });
    }

    protected BranchSummary doCreateBranch() throws Exception {
        return doCreateBranch(doCreateProject().getId());
    }

    protected PromotionLevelSummary doCreatePromotionLevel() throws Exception {
        return doCreatePromotionLevel(doCreateBranch().getId());
    }

    protected ValidationRunSummary doCreateValidationRun(Status status) throws Exception {
        BranchSummary branch = doCreateBranch();
        return doCreateValidationRun(
                doCreateValidationStamp(branch.getId()).getId(),
                doCreateBuild(branch.getId()).getId(),
                status
        );
    }

    protected ValidationRunSummary doCreateValidationRun(final int validationStampId, final int buildId, final Status status) throws Exception {
        return asAdmin().call(new Callable<ValidationRunSummary>() {
            @Override
            public ValidationRunSummary call() throws Exception {
                return controlService.createValidationRun(
                        buildId,
                        validationStampId,
                        new ValidationRunCreationForm(status, "Validation run", PropertiesCreationForm.create())
                );
            }
        });
    }

    protected BuildSummary doCreateBuild() throws Exception {
        return doCreateBuild(doCreateBranch().getId());
    }

    protected BuildSummary doCreateBuild(final int branchId) throws Exception {
        final String buildName = uid("B");
        return asAdmin().call(new Callable<BuildSummary>() {
            @Override
            public BuildSummary call() throws Exception {
                return controlService.createBuild(branchId, new BuildCreationForm(buildName, "Build", PropertiesCreationForm.create()));
            }
        });
    }

    protected ValidationStampSummary doCreateValidationStamp(final int branchId) throws Exception {
        final String validationStampName = uid("VS");
        return asAdmin().call(new Callable<ValidationStampSummary>() {
            @Override
            public ValidationStampSummary call() throws Exception {
                return managementService.createValidationStamp(branchId, new ValidationStampCreationForm(validationStampName, "Validation stamp"));
            }
        });
    }

    protected PromotionLevelSummary doCreatePromotionLevel(final int branchId) throws Exception {
        final String promotionLevelName = uid("PRL");
        return asAdmin().call(new Callable<PromotionLevelSummary>() {
            @Override
            public PromotionLevelSummary call() throws Exception {
                return managementService.createPromotionLevel(branchId, new PromotionLevelCreationForm(promotionLevelName, "Promotion level"));
            }
        });
    }

    protected static interface ContextCall {

        <T> T call(Callable<T> call) throws Exception;
    }

    protected static abstract class AbstractContextCall implements ContextCall {

        @Override
        public <T> T call(Callable<T> call) throws Exception {
            // Gets the current context
            SecurityContext oldContext = SecurityContextHolder.getContext();
            try {
                // Sets the new context
                contextSetup();
                // Call
                return call.call();
            } catch (Exception e) {
                throw e;
            } finally {
                // Restores the context
                SecurityContextHolder.setContext(oldContext);
            }
        }

        protected abstract void contextSetup();
    }

    protected static class AnonymousCall extends AbstractContextCall {

        @Override
        protected void contextSetup() {
            SecurityContextHolder.clearContext();
        }
    }

    protected static class AccountCall extends AbstractContextCall {

        protected final Account account;

        public AccountCall(Account account) {
            this.account = account;
        }

        @Override
        protected void contextSetup() {
            SecurityContext context = new SecurityContextImpl();
            Authentication authentication = new AccountAuthentication(account);
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        }
    }

    protected static class UserCall extends AccountCall {

        public UserCall() {
            super(new Account(1, "user", "Normal user",
                    "user@test.com", SecurityRoles.USER, "builtin", Locale.ENGLISH));
        }

        public UserCall withGlobalFn(GlobalFunction fn) {
            account.withGlobalACL(fn);
            return this;
        }

        public UserCall withProjectFn(ProjectFunction fn, int id) {
            account.withProjectACL(fn, id);
            return this;
        }
    }

    protected static class AdminCall extends AccountCall {

        public AdminCall() {
            super(new Account(1, "admin", "Administrator",
                    "admin@test.com", SecurityRoles.ADMINISTRATOR, "builtin", Locale.ENGLISH));
        }
    }

}
