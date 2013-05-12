package net.ontrack.acceptance.mapping;

import net.ontrack.acceptance.steps.GeneralSteps;
import net.thucydides.core.annotations.Steps;
import org.jbehave.core.annotations.When;

public class GeneralMapping {

    @Steps
    private GeneralSteps generalSteps;

    @When("I am on the home page")
    public void home_page() {
        generalSteps.open_home_page();
    }

}
