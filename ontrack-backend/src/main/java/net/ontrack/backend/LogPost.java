package net.ontrack.backend;

import net.ontrack.core.RunProfile;
import net.ontrack.core.model.Message;
import net.ontrack.service.model.MessageChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({RunProfile.DEV, RunProfile.IT, RunProfile.TEST})
public class LogPost extends AbstractMessagePost {

    private final Logger logger = LoggerFactory.getLogger(LogPost.class);

    @Override
    public boolean supports(MessageChannel channel) {
        return true;
    }

    @Override
    public void post(Message message, String destination) {
        logger.info(
                "[message] Sending message to '{}':\n" +
                        "-----------------\n" +
                        "{}\n" +
                        "\n" +
                        "{}" +
                        "\n" +
                        "-----------------\n",
                new Object[]{
                        destination,
                        message.getTitle(),
                        message.getContent()});
    }

}
