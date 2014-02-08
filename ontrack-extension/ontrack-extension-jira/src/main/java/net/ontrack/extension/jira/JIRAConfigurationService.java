package net.ontrack.extension.jira;

import net.ontrack.extension.jira.service.model.JIRAConfiguration;

import java.util.List;

/**
 * Management of the JIRA configurations.
 */
public interface JIRAConfigurationService {

    List<JIRAConfiguration> getAllConfigurations();

}
