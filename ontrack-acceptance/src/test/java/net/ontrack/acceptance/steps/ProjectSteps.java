package net.ontrack.acceptance.steps;

import net.ontrack.acceptance.dialog.ProjectCreationDialog;
import net.ontrack.acceptance.pages.HomePage;
import net.ontrack.acceptance.pages.ProjectPage;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;

import static org.junit.Assert.assertEquals;

public class ProjectSteps extends AbstractSteps {

    private final HomePage homePage;
    private final ProjectPage projectPage;

    public ProjectSteps(Pages pages) {
        super(pages);
        homePage = getPages().get(HomePage.class);
        projectPage = getPages().get(ProjectPage.class);
    }

    @Step
    public void create_project(String name, String description) {
        ProjectCreationDialog dialog = homePage.openProjectCreationDialog();
        dialog.setNameAndDescription(name, description);
        dialog.submit();
    }

    @Step
    public void project_page_check(String name) {
        String projectName = projectPage.getProjectName();
        assertEquals(String.format("Not on the %s project page", name), name, projectName);
    }
}
