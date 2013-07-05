package net.ontrack.acceptance.pages;

import com.google.common.collect.Lists;
import net.thucydides.core.annotations.DefaultUrl;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

@DefaultUrl("http://localhost:8080/gui/project/{1}/branch/{2}/promotion_level/{3}")
public class PromotionLevelPage extends AbstractPage {

    public PromotionLevelPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void waitForLoad() {
        waitFor("#promotions");
    }

    public List<String> getValidationStamps() {
        return Lists.transform(
                findAll(By.cssSelector("#promotion-validation-stamps-content td")),
                elGetTextFn
        );
    }
}
