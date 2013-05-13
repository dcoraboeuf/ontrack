package net.ontrack.acceptance.mapping;

import net.ontrack.acceptance.steps.DataSteps;
import net.thucydides.core.annotations.Steps;
import org.jbehave.core.annotations.Given;

public class DataMapping {

    @Steps
    private DataSteps dataSteps;

    @Given("a project $project exists with description \"$description\"")
    public void project_exists(String project, String description) {
        dataSteps.delete_project(project);
        dataSteps.create_project(project, description);
    }

}
