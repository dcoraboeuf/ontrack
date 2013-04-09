package net.ontrack.service.support;

import net.ontrack.core.RunProfile;
import net.ontrack.core.model.Message;
import net.ontrack.service.MessagePost;
import net.ontrack.service.model.MessageChannel;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Profile({RunProfile.TEST, RunProfile.IT})
public class InMemoryPost implements MessagePost {

    private final Map<String, Message> messages = new LinkedHashMap<>();

    /**
     * Supports all channels
     */
    @Override
    public boolean supports(MessageChannel channel) {
        return true;
    }

    @Override
    public synchronized void post(Message message, Collection<String> destination) {
        for (String address : destination) {
            messages.put(address, message);
        }
    }

    public Message getMessage(String destination) {
        return messages.get(destination);
    }

}
