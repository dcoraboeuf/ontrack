package net.ontrack.extension.svn.service;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import net.ontrack.core.model.Ack;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.core.security.GlobalFunction;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.issue.IssueService;
import net.ontrack.extension.issue.IssueServiceConfig;
import net.ontrack.extension.issue.IssueServiceFactory;
import net.ontrack.extension.svn.SubversionExtension;
import net.ontrack.extension.svn.SubversionRepositoryPropertyExtension;
import net.ontrack.extension.svn.dao.RepositoryDao;
import net.ontrack.extension.svn.dao.model.TRepository;
import net.ontrack.extension.svn.service.model.SVNRepository;
import net.ontrack.extension.svn.service.model.SVNRepositoryDeletion;
import net.ontrack.extension.svn.service.model.SVNRepositoryForm;
import net.ontrack.service.ManagementService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class DefaultRepositoryService implements RepositoryService {

    private final ManagementService managementService;
    private final PropertiesService propertiesService;
    private final IssueServiceFactory issueServiceFactory;
    private final RepositoryDao repositoryDao;
    private final SecurityUtils securityUtils;

    // TRepository --> SVNRepository
    private final Function<TRepository, SVNRepository> repositoryFn = new Function<TRepository, SVNRepository>() {
        @Override
        public SVNRepository apply(TRepository t) {
            // Issue service
            final IssueService issueService;
            IssueServiceConfig issueServiceConfig = null;
            String issueServiceName = t.getIssueServiceName();
            final Integer issueServiceConfigId = t.getIssueServiceConfigId();
            if (StringUtils.isNotBlank(issueServiceName)) {
                issueService = issueServiceFactory.getServiceByName(issueServiceName);
                if (issueServiceConfigId == null) {
                    throw new IllegalStateException("[svn] The issue service configuration ID is null but the issue service is defined for the repository " + t.getName());
                }
                issueServiceConfig = issueService.getConfigurationById(issueServiceConfigId);
            } else {
                issueService = null;
            }
            // OK
            return new SVNRepository(
                    t.getId(),
                    t.getName(),
                    t.getUrl(),
                    t.getUser(),
                    t.getBranchPattern(),
                    t.getTagPattern(),
                    t.getTagFilterPattern(),
                    t.getBrowserForPath(),
                    t.getBrowserForRevision(),
                    t.getBrowserForChange(),
                    t.getIndexationInterval(),
                    t.getIndexationStart(),
                    IssueService.summaryFn.apply(issueService),
                    IssueServiceConfig.summaryFn.apply(issueServiceConfig)
            );
        }
    };

    @Autowired
    public DefaultRepositoryService(ManagementService managementService, PropertiesService propertiesService, IssueServiceFactory issueServiceFactory, RepositoryDao repositoryDao, SecurityUtils securityUtils) {
        this.managementService = managementService;
        this.propertiesService = propertiesService;
        this.issueServiceFactory = issueServiceFactory;
        this.repositoryDao = repositoryDao;
        this.securityUtils = securityUtils;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SVNRepository> getAllRepositories() {
        return Lists.transform(
                repositoryDao.findAll(),
                repositoryFn
        );
    }

    @Override
    @Transactional
    public SVNRepository createRepository(SVNRepositoryForm form) {
        securityUtils.checkGrant(GlobalFunction.SETTINGS);
        return repositoryFn.apply(repositoryDao.create(form));
    }

    @Override
    @Transactional
    public SVNRepository updateRepository(int id, SVNRepositoryForm form) {
        securityUtils.checkGrant(GlobalFunction.SETTINGS);
        return repositoryFn.apply(repositoryDao.update(id, form));
    }

    @Override
    @Transactional(readOnly = true)
    public SVNRepository getRepository(int id) {
        return repositoryFn.apply(repositoryDao.getById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public String getPassword(int id) {
        securityUtils.checkGrant(GlobalFunction.SETTINGS);
        return repositoryDao.getPassword(id);
    }

    @Override
    @Transactional(readOnly = true)
    public SVNRepositoryDeletion getConfigurationForDeletion(int id) {
        return new SVNRepositoryDeletion(
                getRepository(id),
                Collections2.transform(
                        propertiesService.findEntityByPropertyValue(
                                Entity.PROJECT,
                                SubversionExtension.EXTENSION,
                                SubversionRepositoryPropertyExtension.NAME,
                                String.valueOf(id)
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
    @Transactional
    public Ack deleteRepository(int id) {
        securityUtils.checkGrant(GlobalFunction.SETTINGS);
        // Removes links to projects
        Collection<Integer> projectIds = propertiesService.findEntityByPropertyValue(
                Entity.PROJECT,
                SubversionExtension.EXTENSION,
                SubversionRepositoryPropertyExtension.NAME,
                String.valueOf(id)
        );
        for (int projectId : projectIds) {
            propertiesService.saveProperty(
                    Entity.PROJECT,
                    projectId,
                    SubversionExtension.EXTENSION,
                    SubversionRepositoryPropertyExtension.NAME,
                    null);
        }
        // OK
        return repositoryDao.delete(id);
    }
}
