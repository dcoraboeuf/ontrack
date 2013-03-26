package net.ontrack.service.model;

import lombok.Data;

@Data
public class MessageDestination {

	private final MessageChannel channel;
	private final String destination;

}
