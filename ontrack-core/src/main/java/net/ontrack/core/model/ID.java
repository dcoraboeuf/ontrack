package net.ontrack.core.model;

import lombok.Data;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonCreator;

public class ID {

    @Data
    public static class IDAck {

        public static IDAck test(boolean b) {
            return new IDAck(b);
        }

        private final boolean success;

        public ID withId(int id) {
            if (success) {
                return ID.success(id);
            } else {
                return ID.failure();
            }
        }

    }

    public static ID failure() {
        return new ID(false, -1);
    }

    public static ID success(int value) {
        return new ID(true, value);
    }

    public static IDAck count(int count) {
        return IDAck.test(count == 1);
    }

    @JsonCreator
    public static ID fromJson(JsonNode node) {
        return new ID(
                node.get("success").getBooleanValue(),
                node.get("value").getIntValue()
        );
    }

    private final boolean success;
    private final int value;

    protected ID(boolean success, int value) {
        this.success = success;
        this.value = value;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getValue() {
        return value;
    }

    public Ack ack() {
        return Ack.validate(isSuccess());
    }

}
