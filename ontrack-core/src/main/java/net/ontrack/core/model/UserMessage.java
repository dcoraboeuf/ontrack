package net.ontrack.core.model;

import lombok.Data;
import net.sf.jstring.Localizable;
import net.sf.jstring.LocalizableMessage;
import net.sf.jstring.NonLocalizable;

@Data
public class UserMessage {

    public static UserMessage none() {
        return new UserMessage(UserMessageType.none, new NonLocalizable(""));
    }
	
	public static UserMessage error(Localizable message) {
		return new UserMessage(UserMessageType.error, message);
	}
	
	public static UserMessage warning(Localizable message) {
		return new UserMessage(UserMessageType.warning, message);
	}
	
	public static UserMessage info(Localizable message) {
		return new UserMessage(UserMessageType.info, message);
	}

    public static UserMessage success(Localizable message) {
        return new UserMessage(UserMessageType.success, message);
    }

    public static UserMessage success(String code, Object... param) {
        return new UserMessage(UserMessageType.success, new LocalizableMessage(code, param));
    }
	
	private final UserMessageType type;
	private final Localizable message;

}
