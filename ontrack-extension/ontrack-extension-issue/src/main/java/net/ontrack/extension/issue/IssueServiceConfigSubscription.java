package net.ontrack.extension.issue;

import java.util.Collection;

public interface IssueServiceConfigSubscription {

    boolean supportsService(String serviceId);

    Collection<? extends IssueServiceConfigSubscriber> getSubscribers(String serviceId, int configId);

}
