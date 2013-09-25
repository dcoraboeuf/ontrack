package net.ontrack.backend;

import net.ontrack.core.validation.ValidationException;
import net.ontrack.test.AbstractIntegrationTest;
import net.sf.jstring.Strings;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;
import java.util.concurrent.Callable;

public abstract class AbstractValidationTest extends AbstractBackendTest {

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

    protected void validateNOK(String message, Callable<?> action) throws Exception {
        try {
            action.call();
            Assert.fail("Validation should have failed");
        } catch (ValidationException ex) {
            Assert.assertEquals(message, ex.getLocalizedMessage(strings, Locale.ENGLISH));
        }
    }

}
