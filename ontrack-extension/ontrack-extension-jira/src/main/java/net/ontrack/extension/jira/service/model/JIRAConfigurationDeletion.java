package net.ontrack.extension.jira.service.model;

import lombok.Data;
import net.ontrack.extension.issue.IssueServiceConfigSubscriber;

import java.util.Collection;

@Data
public class JIRAConfigurationDeletion {

    private final JIRAConfiguration configuration;
    /**
     * List of entities that have a JIRA property associated with them.
     */
    private final Collection<IssueServiceConfigSubscriber> subscribers;

}
