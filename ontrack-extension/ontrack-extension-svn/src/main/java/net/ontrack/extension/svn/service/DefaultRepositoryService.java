package net.ontrack.extension.svn.service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.ontrack.core.model.Ack;
import net.ontrack.core.security.GlobalFunction;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.extension.issue.IssueService;
import net.ontrack.extension.issue.IssueServiceConfig;
import net.ontrack.extension.issue.IssueServiceFactory;
import net.ontrack.extension.svn.dao.RepositoryDao;
import net.ontrack.extension.svn.dao.model.TRepository;
import net.ontrack.extension.svn.service.model.SVNRepository;
import net.ontrack.extension.svn.service.model.SVNRepositoryForm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DefaultRepositoryService implements RepositoryService {

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
    public DefaultRepositoryService(IssueServiceFactory issueServiceFactory, RepositoryDao repositoryDao, SecurityUtils securityUtils) {
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
    @Transactional
    public Ack deleteRepository(int id) {
        securityUtils.checkGrant(GlobalFunction.SETTINGS);
        // TODO Removes links to projects
        // OK
        return repositoryDao.delete(id);
    }
}
