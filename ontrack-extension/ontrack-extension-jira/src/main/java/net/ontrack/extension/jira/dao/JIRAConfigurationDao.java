package net.ontrack.extension.jira.dao;

import net.ontrack.extension.jira.service.model.JIRAConfiguration;

import java.util.List;

/**
 * Access to the <code>EXT_JIRA_CONFIGURATION</code> table.
 */
public interface JIRAConfigurationDao {


    List<JIRAConfiguration> findAll();
}
