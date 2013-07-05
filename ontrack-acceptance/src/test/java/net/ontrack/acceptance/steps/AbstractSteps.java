package net.ontrack.acceptance.steps;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import org.apache.commons.lang3.StringUtils;

import static org.junit.Assert.fail;

public abstract class AbstractSteps extends ScenarioSteps {

    public AbstractSteps(Pages pages) {
        super(pages);
    }

    protected void any(Iterable<String> collection, final String value, String message) {
        any(
                collection,
                new Predicate<String>() {
                    @Override
                    public boolean apply(String candidate) {
                        return StringUtils.equals(value, candidate);
                    }
                },
                message);
    }

    protected <T> void any(Iterable<T> collection, Predicate<T> test, String message) {
        if (!Iterables.any(collection, test)) {
            fail(message);
        }
    }

}
