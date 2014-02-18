package net.ontrack.extension.issue.support;

import net.ontrack.extension.issue.IssueServiceConfigRegistry;
import net.ontrack.extension.issue.IssueServiceConfigSubscriber;
import net.ontrack.extension.issue.IssueServiceConfigSubscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class IssueServiceConfigRegistryImpl implements IssueServiceConfigRegistry {

    private final Collection<IssueServiceConfigSubscription> subscriptions;

    @Autowired
    public IssueServiceConfigRegistryImpl(Collection<IssueServiceConfigSubscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    @Override
    public Collection<IssueServiceConfigSubscriber> getSubscribers(String serviceId, int configId) {
        Collection<IssueServiceConfigSubscriber> subscribers = new ArrayList<>();
        for (IssueServiceConfigSubscription subscription : subscriptions) {
            if (subscription.supportsService(serviceId)) {
                subscribers.addAll(subscription.getSubscribers(serviceId, configId));
            }
        }
        return subscribers;
    }

}
