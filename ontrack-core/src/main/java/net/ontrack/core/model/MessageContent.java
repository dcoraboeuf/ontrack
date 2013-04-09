package net.ontrack.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collections;
import java.util.Map;

@Data
@AllArgsConstructor
public class MessageContent {

    private final MessageContentType type;
    private final String text;
    private final Map<String, String> meta;

    public MessageContent(String text) {
        this(MessageContentType.PLAIN, text);
    }

    public MessageContent(MessageContentType type, String text) {
        this(type, text, Collections.<String, String>emptyMap());
    }
}
