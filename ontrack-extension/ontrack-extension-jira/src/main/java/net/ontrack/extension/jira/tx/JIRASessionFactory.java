package net.ontrack.extension.jira.tx;

import net.ontrack.extension.jira.service.model.JIRAConfiguration;

public interface JIRASessionFactory {
    JIRASession create(JIRAConfiguration configuration);
}
