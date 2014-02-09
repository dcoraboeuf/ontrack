package net.ontrack.extension.jira.dao;

import net.ontrack.extension.jira.service.model.JIRAConfiguration;

import java.util.List;
import java.util.Set;

/**
 * Access to the <code>EXT_JIRA_CONFIGURATION</code> table.
 */
public interface JIRAConfigurationDao {


    List<JIRAConfiguration> findAll();

    JIRAConfiguration create(String name, String url, String user, String password, Set<String> excludedProjects, Set<String> excludedIssues);
}
