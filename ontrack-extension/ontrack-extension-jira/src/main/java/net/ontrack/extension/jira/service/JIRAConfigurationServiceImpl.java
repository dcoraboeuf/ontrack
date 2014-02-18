package net.ontrack.extension.jira.service;

import net.ontrack.core.model.Ack;
import net.ontrack.core.security.GlobalFunction;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.extension.issue.IssueServiceConfigRegistry;
import net.ontrack.extension.issue.IssueServiceConfigSubscriber;
import net.ontrack.extension.jira.JIRAConfigurationService;
import net.ontrack.extension.jira.dao.JIRAConfigurationDao;
import net.ontrack.extension.jira.service.model.JIRAConfiguration;
import net.ontrack.extension.jira.service.model.JIRAConfigurationDeletion;
import net.ontrack.extension.jira.service.model.JIRAConfigurationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class JIRAConfigurationServiceImpl implements JIRAConfigurationService {

    private final JIRAConfigurationDao jiraConfigurationDao;
    private final IssueServiceConfigRegistry issueServiceConfigRegistry;
    private final SecurityUtils securityUtils;

    @Autowired
    public JIRAConfigurationServiceImpl(JIRAConfigurationDao jiraConfigurationDao, IssueServiceConfigRegistry issueServiceConfigRegistry, SecurityUtils securityUtils) {
        this.jiraConfigurationDao = jiraConfigurationDao;
        this.issueServiceConfigRegistry = issueServiceConfigRegistry;
        this.securityUtils = securityUtils;
    }

    @Override
    @Transactional(readOnly = true)
    public List<JIRAConfiguration> getAllConfigurations() {
        return jiraConfigurationDao.findAll();
    }

    @Override
    @Transactional
    public JIRAConfiguration createConfiguration(JIRAConfigurationForm configuration) {
        securityUtils.checkGrant(GlobalFunction.SETTINGS);
        return jiraConfigurationDao.create(configuration);
    }

    @Override
    @Transactional
    public JIRAConfiguration updateConfiguration(int id, JIRAConfigurationForm configuration) {
        securityUtils.checkGrant(GlobalFunction.SETTINGS);
        return jiraConfigurationDao.update(
                id,
                configuration
        );
    }

    @Override
    @Transactional
    public Ack deleteConfiguration(int id) {
        securityUtils.checkGrant(GlobalFunction.SETTINGS);
        // Removes associated JIRA subscribers
        issueServiceConfigRegistry.unsubscribe("jira", id);
        // Actual deletion
        return jiraConfigurationDao.delete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public JIRAConfiguration getConfigurationById(int id) {
        return jiraConfigurationDao.getById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public JIRAConfigurationDeletion getConfigurationForDeletion(int id) {
        Collection<IssueServiceConfigSubscriber> subscribers = issueServiceConfigRegistry.getSubscribers("jira", id);
        return new JIRAConfigurationDeletion(
                getConfigurationById(id),
                subscribers
        );
    }

    @Override
    @Transactional(readOnly = true)
    public JIRAConfiguration getConfigurationByName(String name) {
        return jiraConfigurationDao.getByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public String getPassword(int id) {
        securityUtils.checkGrant(GlobalFunction.SETTINGS);
        return jiraConfigurationDao.getPassword(id);
    }
}
