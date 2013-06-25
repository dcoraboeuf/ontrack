package net.ontrack.core.support;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageAnnotation {

    private final String type;
    private final Map<String, String> attributes;

    protected MessageAnnotation(String type) {
        this(type, new HashMap<String, String>());
    }

    public static MessageAnnotation of(String type) {
        return new MessageAnnotation(type);
    }

    public static MessageAnnotation text(String text) {
        return new MessageAnnotation("text").attr("text", text);
    }

    public static MessageAnnotation empty() {
        return new MessageAnnotation("node");
    }

    public MessageAnnotation attr(String name, String value) {
        attributes.put(name, value);
        return this;
    }

    public String attr(String name) {
        return attributes.get(name);
    }
}
