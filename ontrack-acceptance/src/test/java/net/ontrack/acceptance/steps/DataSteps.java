package net.ontrack.acceptance.steps;

import net.ontrack.client.ManageUIClient;
import net.ontrack.client.support.ClientFactory;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import org.apache.commons.lang3.StringUtils;

public class DataSteps extends ScenarioSteps {

    private final ManageUIClient manageUIClient;

    public DataSteps(Pages pages) {
        super(pages);
        // Base URL
        String url = System.getProperty("webdriver.base.url");
        if (StringUtils.isBlank(url)) {
            throw new IllegalStateException("No default URL defined at 'webdriver.base.url'");
        }
        // Client factory
        ClientFactory clientFactory = ClientFactory.create(url);
        // Clients
        manageUIClient = clientFactory.manage();
    }

    @Step
    public void create_project(String project) {
        //To change body of created methods use File | Settings | File Templates.
    }
}
