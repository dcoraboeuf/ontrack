package net.ontrack.core.support;

import com.google.common.base.Function;

public final class FunctionsUtils {

    private FunctionsUtils() {
    }

    public static <I, O> Function<I, O> optional(final Function<I, O> f) {
        return new Function<I, O>() {
            @Override
            public O apply(I input) {
                if (input == null) {
                    return null;
                } else {
                    return f.apply(input);
                }
            }
        };
    }
}
