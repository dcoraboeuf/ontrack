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

    @Given("the project $project does not exist")
    public void project_does_not_exist(String project) {
        dataSteps.delete_project(project);
    }

    @Given("a user is defined with name \"$name\", full name \"$fullName\" and password \"$password\"")
    public void user_defined(String name, String fullName, String password) {
        dataSteps.define_user(name, fullName, password);
    }

}
