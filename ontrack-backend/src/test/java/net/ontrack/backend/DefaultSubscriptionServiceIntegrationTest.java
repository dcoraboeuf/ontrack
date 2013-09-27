package net.ontrack.backend;

import net.ontrack.core.model.*;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.service.*;
import net.ontrack.service.support.InMemoryPost;
import net.ontrack.test.Helper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.concurrent.Callable;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DefaultSubscriptionServiceIntegrationTest extends AbstractBackendTest {

    @Autowired
    private AccountService accountService;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private ManagementService managementService;
    @Autowired
    private ControlService controlService;
    @Autowired
    private EventService eventService;
    @Autowired
    private InMemoryPost inMemoryPost;

    @Test
    public void publish() throws Exception {
        // Unique names
        final String accountName = Helper.uid("ACC");
        final String projectName = Helper.uid("PRJ");
        final String branchName = Helper.uid("BCH");
        final String buildName = Helper.uid("BLD");
        // Prerequisites
        // Creates a branch
        final BranchSummary branch = asAdmin().call(new Callable<BranchSummary>() {
            @Override
            public BranchSummary call() throws Exception {
                // Creates a project
                ProjectSummary project = managementService.createProject(new ProjectCreationForm(projectName, ""));
                // Creates a branch
                return managementService.createBranch(project.getId(), new BranchCreationForm(branchName, ""));
            }
        });
        // Creates an account and subscribes him to the branch
        // Creates the account
        final Account account = asAdmin().call(new Callable<Account>() {
            @Override
            public Account call() throws Exception {
                ID id = accountService.createAccount(new AccountCreationForm(
                        accountName,
                        "Test account",
                        "test@test.com",
                        SecurityRoles.USER,
                        "builtin",
                        "pwd",
                        "pwd"
                ));
                accountService.changeLanguage(id.getValue(), "fr");
                return accountService.getAccount(id.getValue());
            }
        });
        // Subscribes this account to the branch events
        asAccount(account).call(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                subscriptionService.subscribe(Collections.singletonMap(Entity.BRANCH, branch.getId()));
                return null;
            }
        });
        // Creates a build
        asAdmin().call(new Callable<BuildSummary>() {
            @Override
            public BuildSummary call() throws Exception {
                // Creates a build
                return controlService.createBuild(branch.getId(), new BuildCreationForm(buildName, "", PropertiesCreationForm.create()));
            }
        });
        // Forces the publication of events
        eventService.run();
        // Checks the posts
        Message message = inMemoryPost.getMessage("test@test.com");
        assertNotNull("No message received", message);
        assertEquals(
                format(
                        "ontrack  - Le build %s a été créé pour la branche %s du projet %s.",
                        buildName,
                        branchName,
                        projectName
                ),
                message.getTitle());
        assertEquals("HTML", message.getContent().getType().toString());
        String expectedHtml = format(
                Helper.getResourceAsString("/net/ontrack/backend/DefaultSubscriptionServiceIntegrationTest-message.html"),
                buildName,
                branchName,
                projectName,
                branch.getId()
        );
        assertEquals(expectedHtml, message.getContent().getText());
    }

}
