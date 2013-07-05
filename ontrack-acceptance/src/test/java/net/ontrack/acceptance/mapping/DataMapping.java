package net.ontrack.acceptance.mapping;

import net.ontrack.acceptance.support.DataSupport;
import net.thucydides.core.annotations.Steps;
import org.jbehave.core.annotations.Given;

public class DataMapping {

    @Steps
    private DataSupport dataSteps;

    @Given("a project $project exists with description \"$description\"")
    public void project_exists(String project, String description) {
        dataSteps.delete_project(project);
        dataSteps.create_project(project, description);
    }

    @Given("the project $project does not exist")
    public void project_does_not_exist(String project) {
        dataSteps.delete_project(project);
    }

    @Given("a promotion level $project/$branch/$promotionLevel exists with description \"$description\"")
    public void promotion_level_exist(String project, String branch, String promotionLevel, String description) {
        dataSteps.ensure_project_exists(project);
        dataSteps.ensure_branch_exists(project, branch);
        dataSteps.delete_promotion_level(project, branch, promotionLevel);
        dataSteps.create_promotion_level(project, branch, promotionLevel, description);
    }

    @Given("a validation stamp $project/$branch/$validationStamp exists with description \"$description\"")
    public void validation_stamp_exist(String project, String branch, String validationStamp, String description) {
        dataSteps.ensure_project_exists(project);
        dataSteps.ensure_branch_exists(project, branch);
        dataSteps.delete_validation_stamp(project, branch, validationStamp);
        dataSteps.create_validation_stamp(project, branch, validationStamp, description);
    }

    @Given("the validation stamp $project/$branch/$validationStamp is associated with $promotionLevel")
    public void validation_stamp_associated_to_promotion_level(String project, String branch, String validationStamp, String promotionLevel) {
        dataSteps.associate_validation_stamp_with_promotion_level(project, branch, validationStamp, promotionLevel);
    }

    @Given("a user is defined with name \"$name\", full name \"$fullName\" and password \"$password\"")
    public void user_defined(String name, String fullName, String password) {
        dataSteps.define_user(name, fullName, password);
    }

}
