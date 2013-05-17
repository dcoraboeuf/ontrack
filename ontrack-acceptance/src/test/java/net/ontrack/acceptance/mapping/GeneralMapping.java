package net.ontrack.acceptance.mapping;

import net.ontrack.acceptance.steps.GeneralSteps;
import net.thucydides.core.annotations.Steps;
import org.apache.commons.lang3.StringUtils;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

public class GeneralMapping {

    @Steps
    private GeneralSteps generalSteps;

    @When("I am on the home page")
    public void home_page() {
        generalSteps.open_home_page();
    }

    @When("I am not logged")
    public void general_not_logged() {
        generalSteps.general_not_logged();
    }

    @When("I am logged as \"$user\"")
    public void general_logged(String user) {
        // Collects the password
        String password = getPasswordFor(user);
        // Login
        generalSteps.general_login(user, password);
    }

    @Then("I see the $project project with description \"$description\"")
    public void home_project_exists(String project, String description) {
        // TODO Checks the description
        generalSteps.home_project_exists(project);
    }

    @Then("I cannot create a project")
    public void home_cannot_create_project() {
        generalSteps.home_cannot_create_project();
    }

    private String getPasswordFor(String user) {
        if ("admin".equals(user)) {
            return getAdminPassword();
        } else {
            return user;
        }
    }

    private String getAdminPassword() {
        String pwd = System.getProperty("itAdminPassword");
        if (StringUtils.isNotBlank(pwd)) {
            return pwd;
        } else {
            return "admin";
        }
    }

}
