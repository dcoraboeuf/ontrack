package net.ontrack.acceptance.pages;

import org.openqa.selenium.WebDriver;

public class LoginPage extends AbstractPage {

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void waitForLoad() {
        waitFor("#username");
    }

    public void login(String user, String password) {
        $("#username").type(user);
        $("#password").type(password);
        $("#login-submit").click();
    }
}
