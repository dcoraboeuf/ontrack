package net.ontrack.acceptance.steps;

import net.ontrack.acceptance.pages.HomePage;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertNotNull;

public class GeneralSteps extends ScenarioSteps {

    private final HomePage homePage;

    public GeneralSteps(Pages pages) {
        super(pages);
        homePage = getPages().get(HomePage.class);
    }

    @Step
    public void open_home_page() {
        homePage.open();
    }

    @Step
    public void home_project_exists(String project) {
        WebElement link = homePage.projectLink(project);
        assertNotNull(link);
    }
}
