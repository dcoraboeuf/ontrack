package net.ontrack.extension.jira.tx;

import net.ontrack.core.model.Ack;
import net.ontrack.extension.jira.service.model.JIRAConfiguration;
import net.ontrack.extension.jira.service.model.JIRAConfigurationForm;

public interface JIRASessionFactory {
    JIRASession create(JIRAConfiguration configuration);

    Ack testConfiguration(JIRAConfigurationForm form);
}
