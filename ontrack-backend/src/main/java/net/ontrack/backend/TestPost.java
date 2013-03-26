package net.ontrack.backend;

import net.ontrack.core.RunProfile;
import net.ontrack.core.model.Message;
import net.ontrack.service.model.MessageChannel;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Collects all the messages in the in-memory box, used for test only
 */
@Component
@Profile({RunProfile.TEST, RunProfile.IT})
public class TestPost extends AbstractMessagePost {

    private final Map<String, Message> messages = new LinkedHashMap<>();

    /**
     * Supports all channels
     */
    @Override
    public boolean supports(MessageChannel channel) {
        return true;
    }

    @Override
    public synchronized void post(Message message, String destination) {
        messages.put(destination, message);
    }

    public Message getMessage(String email) {
        return messages.get(email);
    }

}
