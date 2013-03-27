package net.ontrack.backend;

import net.ontrack.core.model.Message;
import net.ontrack.service.MessagePost;
import net.ontrack.service.MessageService;
import net.ontrack.service.model.MessageChannel;
import net.ontrack.service.model.MessageDestination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;

@Service
public class DefaultMessageService implements MessageService {

    private final ApplicationContext applicationContext;

	private Collection<MessagePost> posts;

    @Autowired
    public DefaultMessageService(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        posts = applicationContext.getBeansOfType(MessagePost.class).values();
    }

	@Override
	public void sendMessage(Message message, MessageDestination messageDestination) {
		MessageChannel channel = messageDestination.getChannel();
		String destination = messageDestination.getDestination();
		for (MessagePost post : posts) {
			if (post.supports(channel)) {
				post.post(message, destination);
			}
		}
	}

}
