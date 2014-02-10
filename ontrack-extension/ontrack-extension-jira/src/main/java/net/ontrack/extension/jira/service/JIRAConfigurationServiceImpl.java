package net.ontrack.extension.jira.service;

import net.ontrack.core.model.Ack;
import net.ontrack.core.security.GlobalFunction;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.extension.jira.JIRAConfigurationService;
import net.ontrack.extension.jira.dao.JIRAConfigurationDao;
import net.ontrack.extension.jira.service.model.JIRAConfiguration;
import net.ontrack.extension.jira.service.model.JIRAConfigurationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JIRAConfigurationServiceImpl implements JIRAConfigurationService {

    private final JIRAConfigurationDao jiraConfigurationDao;
    private final SecurityUtils securityUtils;

    @Autowired
    public JIRAConfigurationServiceImpl(JIRAConfigurationDao jiraConfigurationDao, SecurityUtils securityUtils) {
        this.jiraConfigurationDao = jiraConfigurationDao;
        this.securityUtils = securityUtils;
    }

    @Override
    @Transactional(readOnly = true)
    public List<JIRAConfiguration> getAllConfigurations() {
        securityUtils.checkGrant(GlobalFunction.SETTINGS);
        return jiraConfigurationDao.findAll();
    }

    @Override
    @Transactional
    public JIRAConfiguration createConfiguration(JIRAConfigurationForm configuration) {
        securityUtils.checkGrant(GlobalFunction.SETTINGS);
        return jiraConfigurationDao.create(
                configuration.getName(),
                configuration.getUrl(),
                configuration.getUser(),
                configuration.getPassword(),
                configuration.getExcludedProjects(),
                configuration.getExcludedIssues()
        );
    }

    @Override
    @Transactional
    public JIRAConfiguration updateConfiguration(int id, JIRAConfigurationForm configuration) {
        securityUtils.checkGrant(GlobalFunction.SETTINGS);
        return jiraConfigurationDao.update(
                id,
                configuration.getName(),
                configuration.getUrl(),
                configuration.getUser(),
                configuration.getPassword(),
                configuration.getExcludedProjects(),
                configuration.getExcludedIssues()
        );
    }

    @Override
    @Transactional
    public Ack deleteConfiguration(int id) {
        securityUtils.checkGrant(GlobalFunction.SETTINGS);
        return jiraConfigurationDao.delete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public JIRAConfiguration getConfigurationById(int id) {
        securityUtils.checkGrant(GlobalFunction.SETTINGS);
        return jiraConfigurationDao.getById(id);
    }
}
