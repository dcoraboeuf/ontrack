package net.ontrack.extension.jira;

import net.ontrack.core.model.Ack;
import net.ontrack.extension.jira.service.model.JIRAConfiguration;
import net.ontrack.extension.jira.service.model.JIRAConfigurationForm;

import java.util.List;

/**
 * Management of the JIRA configurations.
 */
public interface JIRAConfigurationService {

    List<JIRAConfiguration> getAllConfigurations();

    JIRAConfiguration createConfiguration(JIRAConfigurationForm configuration);

    JIRAConfiguration updateConfiguration(int id, JIRAConfigurationForm configuration);

    Ack deleteConfiguration(int id);

    JIRAConfiguration getConfigurationById(int id);
}
