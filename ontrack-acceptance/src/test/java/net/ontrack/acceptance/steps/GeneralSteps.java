package net.ontrack.acceptance.steps;

import net.ontrack.acceptance.pages.HeaderPage;
import net.ontrack.acceptance.pages.HomePage;
import net.ontrack.acceptance.pages.LoginPage;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.*;

public class GeneralSteps extends ScenarioSteps {

    private final HeaderPage headerPage;
    private final HomePage homePage;

    public GeneralSteps(Pages pages) {
        super(pages);
        homePage = getPages().get(HomePage.class);
        headerPage = getPages().get(HeaderPage.class);
    }

    @Step
    public void general_not_logged() {
        if (headerPage.isLogged()) {
            headerPage.signOut();
        }
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

    @Step
    public void home_cannot_create_project() {
        WebElement button = homePage.getCreateProjectButton();
        assertNull(button);
    }

    @Step
    public void general_login(String user, String password) {
        // Makes sure we are  not logged
        general_not_logged();
        // Logs in
        LoginPage loginPage = headerPage.signIn();
        // Enters credentials and validates
        loginPage.login(user, password);
        // Validates we are logged in
        assertTrue("Cannot log user " + user, headerPage.isLogged());
    }

    @Step
    public void general_no_user_name() {
        String fullName = headerPage.getSignedUserName();
        assertNull("User is logged as " + fullName, fullName);
    }
}
