package net.ontrack.acceptance.pages;

import net.thucydides.core.pages.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class AbstractPage extends PageObject {

    public AbstractPage(WebDriver driver) {
        super(driver);
    }

    public WebElement findOptional(By selector) {
        try {
            return find(selector);
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    public boolean isDefined(By selector) {
        return findOptional(selector) != null;
    }

    protected <T extends AbstractPage> T switchAndLoad(Class<T> pageClass) {
        T page = switchToPage(pageClass);
        page.waitForLoad();
        return page;
    }

    public abstract void waitForLoad();

}
