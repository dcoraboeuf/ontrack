package net.ontrack.backend;

import com.google.common.base.Predicate;

public interface ValidatorService {

    <T> void validate(T value, Predicate<T> predicate, String code, Object... parameters);

    void validate(final Object o, Class<?> group);

}
