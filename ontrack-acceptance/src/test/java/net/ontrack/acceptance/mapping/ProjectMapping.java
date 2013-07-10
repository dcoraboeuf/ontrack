package net.ontrack.acceptance.mapping;

import net.ontrack.acceptance.steps.GeneralSteps;
import net.ontrack.acceptance.steps.ProjectSteps;
import net.thucydides.core.annotations.Steps;
import org.jbehave.core.annotations.When;

public class ProjectMapping {

    @Steps
    private GeneralSteps generalSteps;

    @Steps
    private ProjectSteps projectSteps;

    @When("I create a $name project with description \"$description\"")
    public void create_project(String name, String description) {
        generalSteps.open_home_page();
        projectSteps.create_project(name, description);
    }

}
