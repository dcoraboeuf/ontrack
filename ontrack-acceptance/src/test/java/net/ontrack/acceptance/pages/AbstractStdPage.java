package net.ontrack.acceptance.pages;

import org.openqa.selenium.WebDriver;

public abstract class AbstractStdPage extends AbstractPage {

    public AbstractStdPage(WebDriver driver) {
        super(driver);
    }

    public void close() {
        $(".page-title").then(".icon-remove").click();
    }
}
