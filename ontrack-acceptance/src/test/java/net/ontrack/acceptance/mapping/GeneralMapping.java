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

    @Then("I see the $project project")
    public void home_project_exists(String project) {
        generalSteps.home_project_exists(project);
    }

}
