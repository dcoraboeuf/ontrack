package net.ontrack.core.model;

import com.google.common.base.Function;
import lombok.Data;

@Data
public class EntityStub {

    public static final Function<EntityStub, Integer> FN_GET_ID = new Function<EntityStub, Integer>() {
        @Override
        public Integer apply(EntityStub stub) {
            return stub.getId();
        }
    };

    private final Entity entity;
    private final int id;
    private final String name;

}
