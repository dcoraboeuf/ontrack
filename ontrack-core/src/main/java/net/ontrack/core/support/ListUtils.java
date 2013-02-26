package net.ontrack.core.support;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ListUtils {

    private ListUtils() {
    }

    public static <T extends Comparable<T>> List<T> concat(List<T> source, T element) {
        List<T> target = new ArrayList<T>(source);
        target.add(element);
        Collections.sort(target);
        return ImmutableList.copyOf(target);
    }
}
