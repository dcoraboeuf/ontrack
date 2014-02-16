package net.ontrack.extension.svn.service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.ontrack.core.model.Ack;
import net.ontrack.core.security.GlobalFunction;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.extension.svn.dao.RepositoryDao;
import net.ontrack.extension.svn.dao.model.TRepository;
import net.ontrack.extension.svn.service.model.SVNRepository;
import net.ontrack.extension.svn.service.model.SVNRepositoryForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DefaultRepositoryService implements RepositoryService {

    private final RepositoryDao repositoryDao;
    private final SecurityUtils securityUtils;

    // TRepository --> SVNRepository
    private final Function<TRepository, SVNRepository> repositoryFn = new Function<TRepository, SVNRepository>() {
        @Override
        public SVNRepository apply(TRepository t) {
            return new SVNRepository(
                    t.getId(),
                    t.getName(),
                    t.getUrl(),
                    t.getUser(),
                    t.getPassword(),
                    t.getBranchPattern(),
                    t.getTagPattern(),
                    t.getTagFilterPattern(),
                    t.getBrowserForPath(),
                    t.getBrowserForRevision(),
                    t.getBrowserForChange(),
                    t.getIndexationInterval(),
                    t.getIndexationStart(),
                    // FIXME Issue service
                    null,
                    // FIXME Issue service config
                    null
            );
        }
    };

    @Autowired
    public DefaultRepositoryService(RepositoryDao repositoryDao, SecurityUtils securityUtils) {
        this.repositoryDao = repositoryDao;
        this.securityUtils = securityUtils;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SVNRepository> getAllRepositories() {
        securityUtils.checkGrant(GlobalFunction.SETTINGS);
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
        securityUtils.checkGrant(GlobalFunction.SETTINGS);
        return repositoryFn.apply(repositoryDao.getById(id));
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
