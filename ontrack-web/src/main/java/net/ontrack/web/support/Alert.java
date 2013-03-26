package net.ontrack.web.support;

import lombok.Data;
import net.ontrack.core.model.UserMessageType;

@Data
public class Alert {

    private final UserMessageType type;
    private final String message;

}
