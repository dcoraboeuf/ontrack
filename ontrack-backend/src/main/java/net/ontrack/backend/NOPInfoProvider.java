package net.ontrack.backend;

import net.ontrack.core.model.UserMessage;
import net.ontrack.service.InfoProvider;
import org.springframework.stereotype.Component;

/**
 * At least an {@link InfoProvider} must be declared. Do not rely on extensions being available.
 */
@Component
public class NOPInfoProvider implements InfoProvider {
    @Override
    public UserMessage getInfo() {
        return null;
    }
}
