package net.ontrack.core.model;

import lombok.Data;

@Data
public class UserMessage {
	
	public static UserMessage error(String message) {
		return new UserMessage(UserMessageType.error, message);
	}
	
	public static UserMessage warning(String message) {
		return new UserMessage(UserMessageType.warning, message);
	}
	
	public static UserMessage info(String message) {
		return new UserMessage(UserMessageType.info, message);
	}
	
	public static UserMessage success(String message) {
		return new UserMessage(UserMessageType.success, message);
	}
	
	private final UserMessageType type;
	private final String message;

}
