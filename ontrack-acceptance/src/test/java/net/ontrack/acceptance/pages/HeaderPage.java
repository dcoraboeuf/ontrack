package net.ontrack.acceptance.pages;

import net.thucydides.core.annotations.findby.By;
import net.thucydides.core.pages.PageObject;
import org.openqa.selenium.WebDriver;

public class HeaderPage extends AbstractPage {

    public HeaderPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLogged() {
        return isDefined(By.id("header-signin"));
    }

    public void signOut() {
        // FIXME Implement net.ontrack.acceptance.pages.HeaderPage.signOut

    }
}
