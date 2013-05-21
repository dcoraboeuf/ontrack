package net.ontrack.acceptance.mapping;

import net.ontrack.acceptance.steps.GeneralSteps;
import net.ontrack.acceptance.support.AccSupport;
import net.thucydides.core.annotations.Steps;
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

    @Then("I do not see my user name")
    public void general_no_user_name() {
        generalSteps.general_no_user_name();
    }

    @Then("I see I am connected as \"$fullName\"")
    public void general_user_name(String fullName) {
        generalSteps.general_user_name(fullName);
    }

    private String getPasswordFor(String user) {
        if ("admin".equals(user)) {
            return AccSupport.getAdminPassword();
        } else {
            return user;
        }
    }

}
