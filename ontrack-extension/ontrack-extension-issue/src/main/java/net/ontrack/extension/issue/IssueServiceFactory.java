package net.ontrack.extension.issue;

import com.google.common.base.Optional;

import java.util.Collection;

public interface IssueServiceFactory {

    IssueService getServiceByName(String name);

    Optional<IssueService> getOptionalServiceByName(String name);

    Collection<IssueServiceSummary> getAllServices();

}
