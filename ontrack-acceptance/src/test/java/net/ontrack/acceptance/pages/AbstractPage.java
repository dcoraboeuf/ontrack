package net.ontrack.acceptance.pages;

import com.google.common.base.Function;
import net.thucydides.core.annotations.WhenPageOpens;
import net.thucydides.core.pages.PageObject;
import net.thucydides.core.pages.PageUrls;
import net.thucydides.core.pages.WebElementFacade;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class AbstractPage extends PageObject {

    protected static final Function<WebElementFacade,String> elGetTextFn = new Function<WebElementFacade, String>() {
        @Override
        public String apply(WebElementFacade el) {
            return el.getText();
        }
    };

    public AbstractPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void setPageUrls(final PageUrls pageUrls) {
        super.setPageUrls(new PageUrls(this) {
            @Override
            public String getStartingUrl(String... parameterValues) {
                String url = super.getStartingUrl(parameterValues);
                // Workaround for https://java.net/jira/browse/THUCYDIDES-142
                url = StringUtils.replace(url, "ontrack/ontrack", "ontrack");
                // OK
                return url;
            }
        });
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

    @WhenPageOpens
    public void checkForError() {
        WebElement error = findOptional(By.id("error-page-message"));
        if (error != null) {
            throw new ErrorPageException(error.getText());
        }
    }

}
