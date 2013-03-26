package net.ontrack.service;

import net.ontrack.core.model.Message;
import net.ontrack.service.model.MessageChannel;

public interface MessagePost {
	
	boolean supports(MessageChannel channel);
	
	void post(Message message, String destination);

}
