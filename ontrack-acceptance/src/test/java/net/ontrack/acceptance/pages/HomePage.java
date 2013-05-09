package net.ontrack.acceptance.pages;

import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.pages.PageObject;
import org.openqa.selenium.WebDriver;

@DefaultUrl("http://localhost:9999/ontrack")
public class HomePage extends PageObject {

    public HomePage(WebDriver driver) {
        super(driver);
    }

}
