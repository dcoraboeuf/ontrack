package net.ontrack.acceptance.pages;

import net.thucydides.core.annotations.DefaultUrl;
import org.openqa.selenium.WebDriver;

@DefaultUrl("http://localhost:8080/gui/project/{1}/branch/{2}/promotion_level/{3}")
public class PromotionLevelPage extends AbstractPage {

    public PromotionLevelPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void waitForLoad() {
        waitFor("#promotions");
    }
}
