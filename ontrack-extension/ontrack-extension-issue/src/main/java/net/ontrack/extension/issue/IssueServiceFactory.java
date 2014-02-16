package net.ontrack.extension.issue;

import java.util.Collection;

public interface IssueServiceFactory {

    IssueService getServiceByName(String name);

    Collection<IssueServiceSummary> getAllServices();

}
