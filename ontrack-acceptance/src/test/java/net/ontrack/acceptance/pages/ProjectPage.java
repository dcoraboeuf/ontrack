package net.ontrack.acceptance.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProjectPage extends AbstractStdPage {

    public ProjectPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void waitForLoad() {
        waitForRenderedElements(By.cssSelector("span.title"));
    }

    public String getProjectName() {
        return find(By.cssSelector("span.title")).getText();
    }
}
