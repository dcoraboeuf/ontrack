package net.ontrack.backend;

import net.ontrack.backend.security.AccountAuthentication;
import net.ontrack.core.model.*;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.service.ManagementService;
import net.ontrack.test.AbstractIntegrationTest;
import net.ontrack.test.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.util.Locale;
import java.util.concurrent.Callable;

public abstract class AbstractBackendTest extends AbstractIntegrationTest {

    @Autowired
    private ManagementService managementService;

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
    }

    protected static class AdminCall extends AccountCall {

        public AdminCall() {
            super(new Account(1, "admin", "Administrator",
                    "admin@test.com", SecurityRoles.ADMINISTRATOR, "builtin", Locale.ENGLISH));
        }
    }

}
