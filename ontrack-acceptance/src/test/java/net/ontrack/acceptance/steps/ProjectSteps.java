package net.ontrack.acceptance.steps;

import net.ontrack.acceptance.dialog.ProjectCreationDialog;
import net.ontrack.acceptance.pages.HomePage;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;

public class ProjectSteps extends AbstractSteps {

    private final HomePage homePage;

    public ProjectSteps(Pages pages) {
        super(pages);
        homePage = getPages().get(HomePage.class);
    }

    @Step
    public void create_project(String name, String description) {
        ProjectCreationDialog dialog = homePage.openProjectCreationDialog();
        dialog.setNameAndDescription(name, description);
        dialog.submit();
    }
}
