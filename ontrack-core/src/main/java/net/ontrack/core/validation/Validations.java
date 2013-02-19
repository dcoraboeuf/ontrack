package net.ontrack.core.validation;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import java.util.Arrays;
import java.util.Collection;

public final class Validations {

    private Validations() {
    }

    public static <T> Predicate<T> oneOf(T... values) {
        return oneOf(Arrays.asList(values));
    }

    public static <T> Predicate<T> oneOf(Collection<T> values) {
        return Predicates.in(values);
    }
}
