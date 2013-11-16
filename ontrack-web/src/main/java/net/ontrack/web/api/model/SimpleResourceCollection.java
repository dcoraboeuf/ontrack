package net.ontrack.web.api.model;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class SimpleResourceCollection<R extends Resource>
        extends AbstractResource<SimpleResourceCollection<R>>
        implements ResourceCollection<R> {

    private final List<R> items;

    public static <T, R extends Resource> SimpleResourceCollection<R> of(
            List<T> source,
            Function<T, R> transformFn
    ) {
        return new SimpleResourceCollection<>(
                Lists.transform(
                        source,
                        transformFn
                )
        );
    }

}
