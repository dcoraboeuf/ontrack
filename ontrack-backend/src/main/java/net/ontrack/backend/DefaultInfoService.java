package net.ontrack.backend;

import net.ontrack.core.model.UserMessage;
import net.ontrack.core.model.UserMessageType;
import net.ontrack.service.InfoProvider;
import net.ontrack.service.InfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class DefaultInfoService implements InfoService {

    private final Collection<InfoProvider> infoProviders;

    @Autowired(required = false)
    public DefaultInfoService(Collection<InfoProvider> infoProviders) {
        this.infoProviders = infoProviders;
    }

    @Override
    public UserMessage getInfo() {
        UserMessage info = UserMessage.none();
        if (infoProviders != null) {
            for (InfoProvider infoProvider : infoProviders) {
                UserMessage providedInfo = infoProvider.getInfo();
                if (providedInfo != null && providedInfo.getType() != UserMessageType.none) {
                    info = providedInfo;
                }
            }
        }
        return info;
    }
}
