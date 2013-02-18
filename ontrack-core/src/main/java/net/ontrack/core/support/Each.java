package net.ontrack.core.support;

public final class Each {

    private Each() {
    }

    public static <T> void withIndex(Iterable<T> collection, ItemActionWithIndex<T> action) {
        int index = 0;
        for (T item : collection) {
            action.apply(item, index++);
        }
    }
}
