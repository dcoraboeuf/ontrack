package net.ontrack.backend;

import net.ontrack.core.validation.ValidationException;
import net.ontrack.test.AbstractIntegrationTest;
import net.sf.jstring.Strings;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

public abstract class AbstractValidationTest extends AbstractIntegrationTest {

    @Autowired
    private Strings strings;

    protected void validateNOK(String message, Runnable action) {
        try {
            action.run();
            Assert.fail("Validation should have failed");
        } catch (ValidationException ex) {
            Assert.assertEquals(message, ex.getLocalizedMessage(strings, Locale.ENGLISH));
        }
    }

}
