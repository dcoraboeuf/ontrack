package net.ontrack.acceptance.pages;

import net.thucydides.core.annotations.findby.By;
import org.openqa.selenium.WebDriver;

public class HeaderPage extends AbstractPage {

    public HeaderPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void waitForLoad() {
        waitFor("#header-version");
    }

    public boolean isLogged() {
        return !isDefined(By.id("header-signin"));
    }

    public void signOut() {
        // FIXME Implement net.ontrack.acceptance.pages.HeaderPage.signOut

    }

    public LoginPage signIn() {
        find(By.id("header-signin")).click();
        return switchAndLoad(LoginPage.class);
    }
}
