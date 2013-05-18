package net.ontrack.acceptance.steps;

import net.ontrack.client.AdminUIClient;
import net.ontrack.client.ManageUIClient;
import net.ontrack.client.support.AdminClientCall;
import net.ontrack.client.support.ClientSupport;
import net.ontrack.client.support.ManageClientCall;
import net.ontrack.core.model.AccountCreationForm;
import net.ontrack.core.model.ProjectCreationForm;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.core.security.SecurityRoles;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import org.apache.commons.lang3.StringUtils;

public class DataSteps extends ScenarioSteps {

    private final ClientSupport client;

    public DataSteps(Pages pages) {
        super(pages);
        // Base URL
        String url = System.getProperty("webdriver.base.url");
        if (StringUtils.isBlank(url)) {
            throw new IllegalStateException("No default URL defined at 'webdriver.base.url'");
        }
        // Client support
        client = new ClientSupport(url);
    }

    @Step
    public void create_project(final String project, final String description) {
        client.asUser("admin", "admin", new ManageClientCall<Void>() {
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

    public void delete_project(final String project) {
        client.asUser("admin", "admin", new ManageClientCall<Void>() {
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

    public void define_user(final String name, final String fullName, final String password) {
        client.asUser("admin", "admin", new AdminClientCall<Void>() {
            @Override
            public Void onCall(AdminUIClient ui) {
                ui.createAccount(
                        new AccountCreationForm(
                                name,
                                fullName,
                                name + "@test.com",
                                SecurityRoles.USER,
                                "builtin",
                                password,
                                password
                        )
                );
                return null;
            }
        });
    }
}
