package net.ontrack.service;

import net.ontrack.core.model.Message;
import net.ontrack.service.model.MessageDestination;


public interface MessageService {

	void sendMessage(Message message, MessageDestination messageDestination);

}
