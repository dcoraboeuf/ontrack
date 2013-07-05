package net.ontrack.core.model;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonCreator;

public class Flag {

    public static final Flag SET = new Flag(true);
    public static final Flag UNSET = new Flag(false);
    private final boolean set;

    private Flag(boolean set) {
        this.set = set;
    }

    public static Flag of(boolean flag) {
        return flag ? SET : UNSET;
    }

    @JsonCreator
    public static Flag fromJson(JsonNode node) {
        return Flag.of(node.get("set").getBooleanValue());
    }

    public boolean isSet() {
        return set;
    }
}
