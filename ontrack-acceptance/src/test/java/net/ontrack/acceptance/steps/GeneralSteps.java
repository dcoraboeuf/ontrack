package net.ontrack.acceptance.steps;

import net.ontrack.acceptance.pages.HomePage;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

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
}
