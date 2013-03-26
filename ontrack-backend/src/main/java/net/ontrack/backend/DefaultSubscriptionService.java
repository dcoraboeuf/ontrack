package net.ontrack.backend;

import net.ontrack.core.model.Ack;
import net.ontrack.core.model.Entity;
import net.ontrack.service.SubscriptionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class DefaultSubscriptionService implements SubscriptionService {

    private final ConfigurationService configurationService;

    @Autowired
    public DefaultSubscriptionService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public boolean isEnabled() {
        return StringUtils.isNotBlank(configurationService.get(ConfigurationKey.MAIL_HOST, false, null));
    }

    @Override
    @Transactional
    public Ack subscribe(Map<Entity, Integer> entities) {
        // FIXME subscribe
        return Ack.OK;
    }
}
