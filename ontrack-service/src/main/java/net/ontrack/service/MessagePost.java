package net.ontrack.service;

import net.ontrack.core.model.Message;
import net.ontrack.service.model.MessageChannel;

import java.util.Collection;

public interface MessagePost {
	
	boolean supports(MessageChannel channel);
	
	void post(Message message, Collection<String> destination);

}
