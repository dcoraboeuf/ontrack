package net.ontrack.acceptance.pages;

import net.thucydides.core.pages.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HomePage extends PageObject {

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public WebElement projectLink(String project) {
        try {
            return find(By.id("projects")).find(By.linkText(project));
        } catch (NoSuchElementException ex) {
            return null;
        }
    }
}
