package net.ontrack.extension.jira.dao;

import net.ontrack.core.model.Ack;
import net.ontrack.extension.jira.service.model.JIRAConfiguration;
import net.ontrack.extension.jira.service.model.JIRAConfigurationForm;

import java.util.List;

/**
 * Access to the <code>EXT_JIRA_CONFIGURATION</code> table.
 */
public interface JIRAConfigurationDao {


    List<JIRAConfiguration> findAll();

    JIRAConfiguration create(JIRAConfigurationForm form);

    JIRAConfiguration update(int id, JIRAConfigurationForm form);

    JIRAConfiguration getById(int id);

    JIRAConfiguration getByName(String name);

    Ack delete(int id);

    String getPassword(int id);
}
