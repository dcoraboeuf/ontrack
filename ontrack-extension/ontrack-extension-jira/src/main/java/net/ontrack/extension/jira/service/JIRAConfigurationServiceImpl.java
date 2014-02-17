package net.ontrack.extension.jira.service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.ontrack.core.model.Ack;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.core.security.GlobalFunction;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.jira.JIRAConfigurationPropertyExtension;
import net.ontrack.extension.jira.JIRAConfigurationService;
import net.ontrack.extension.jira.JIRAExtension;
import net.ontrack.extension.jira.dao.JIRAConfigurationDao;
import net.ontrack.extension.jira.service.model.JIRAConfiguration;
import net.ontrack.extension.jira.service.model.JIRAConfigurationDeletion;
import net.ontrack.extension.jira.service.model.JIRAConfigurationForm;
import net.ontrack.service.ManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class JIRAConfigurationServiceImpl implements JIRAConfigurationService {

    private final ManagementService managementService;
    private final PropertiesService propertiesService;
    private final JIRAConfigurationDao jiraConfigurationDao;
    private final SecurityUtils securityUtils;

    @Autowired
    public JIRAConfigurationServiceImpl(ManagementService managementService, PropertiesService propertiesService, JIRAConfigurationDao jiraConfigurationDao, SecurityUtils securityUtils) {
        this.managementService = managementService;
        this.propertiesService = propertiesService;
        this.jiraConfigurationDao = jiraConfigurationDao;
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
        // Removes associated JIRA properties from projects
        Collection<Integer> projectIds = propertiesService.findEntityByPropertyValue(
                Entity.PROJECT,
                JIRAExtension.EXTENSION,
                JIRAConfigurationPropertyExtension.NAME,
                String.valueOf(id)
        );
        for (int projectId : projectIds) {
            propertiesService.saveProperty(Entity.PROJECT, projectId, JIRAExtension.EXTENSION, JIRAConfigurationPropertyExtension.NAME, null);
        }
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
        return new JIRAConfigurationDeletion(
                getConfigurationById(id),
                // Gets the list of projects that have the JIRA property set
                Lists.transform(
                        Lists.newArrayList(
                                propertiesService.findEntityByPropertyValue(
                                        Entity.PROJECT,
                                        JIRAExtension.EXTENSION,
                                        JIRAConfigurationPropertyExtension.NAME,
                                        String.valueOf(id)
                                )
                        ),
                        new Function<Integer, ProjectSummary>() {
                            @Override
                            public ProjectSummary apply(Integer projectId) {
                                return managementService.getProject(projectId);
                            }
                        }
                )
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
