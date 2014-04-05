package net.ontrack.backend;

import net.ontrack.core.model.UserMessage;
import net.ontrack.service.InfoProvider;
import net.ontrack.service.InfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class DefaultInfoService implements InfoService {

    private final Collection<InfoProvider> infoProviders;

    @Autowired(required = false)
    public DefaultInfoService(Collection<InfoProvider> infoProviders) {
        this.infoProviders = infoProviders;
    }

    @Override
    public Collection<UserMessage> getInfo() {
        Collection<UserMessage> messages = new ArrayList<>();
        if (infoProviders != null) {
            for (InfoProvider infoProvider : infoProviders) {
                messages.addAll(infoProvider.getInfo());
            }
        }
        return messages;
    }
}
