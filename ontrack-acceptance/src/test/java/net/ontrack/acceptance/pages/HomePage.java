package net.ontrack.acceptance.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HomePage extends AbstractPage {

    public HomePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void waitForLoad() {
        waitFor("#projects");
    }

    public WebElement projectLink(String project) {
        try {
            return find(By.id("projects")).find(By.linkText(project));
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    public WebElement getCreateProjectButton() {
        return findOptional(By.id("project-create-button"));
    }
}
