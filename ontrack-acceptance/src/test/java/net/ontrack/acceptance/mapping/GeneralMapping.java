package net.ontrack.acceptance.mapping;

import net.ontrack.acceptance.steps.GeneralSteps;
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

    @Then("I see the $project project with description \"$description\"")
    public void home_project_exists(String project, String description) {
        // TODO Checks the description
        generalSteps.home_project_exists(project);
    }

    @Then("I cannot create a project")
    public void home_cannot_create_project() {
        generalSteps.home_cannot_create_project();
    }

}
