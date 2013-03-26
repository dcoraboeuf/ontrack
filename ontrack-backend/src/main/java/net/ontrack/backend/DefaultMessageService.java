package net.ontrack.backend;

import net.ontrack.core.model.Message;
import net.ontrack.service.MessagePost;
import net.ontrack.service.MessageService;
import net.ontrack.service.model.MessageChannel;
import net.ontrack.service.model.MessageDestination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultMessageService implements MessageService {

	private final List<MessagePost> posts;

	@Autowired
	public DefaultMessageService(List<MessagePost> posts) {
		this.posts = posts;
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
