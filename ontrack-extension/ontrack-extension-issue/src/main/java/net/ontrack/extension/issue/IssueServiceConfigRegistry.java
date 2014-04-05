package net.ontrack.extension.issue;

import java.util.Collection;

/**
 * Defines a way to know where an issue service configuration is used.
 */
public interface IssueServiceConfigRegistry {

    Collection<IssueServiceConfigSubscriber> getSubscribers(String serviceId, int configId);

    void unsubscribe(String serviceId, int configId);
}
