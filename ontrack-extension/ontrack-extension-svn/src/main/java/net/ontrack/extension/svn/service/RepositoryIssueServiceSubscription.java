package net.ontrack.extension.svn.service;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import net.ontrack.extension.issue.IssueServiceConfigSubscriber;
import net.ontrack.extension.issue.IssueServiceConfigSubscription;
import net.ontrack.extension.svn.dao.RepositoryDao;
import net.ontrack.extension.svn.dao.model.TRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class RepositoryIssueServiceSubscription implements IssueServiceConfigSubscription {

    private final RepositoryDao repositoryDao;

    @Autowired
    public RepositoryIssueServiceSubscription(RepositoryDao repositoryDao) {
        this.repositoryDao = repositoryDao;
    }

    /**
     * Potentially all issue services are supported
     */
    @Override
    public boolean supportsService(String serviceId) {
        return true;
    }

    @Override
    public Collection<? extends IssueServiceConfigSubscriber> getSubscribers(String serviceId, int configId) {
        return Collections2.transform(
                repositoryDao.findByIssueServiceConfig(serviceId, configId),
                new Function<TRepository, IssueServiceConfigSubscriber>() {
                    @Override
                    public IssueServiceConfigSubscriber apply(TRepository t) {
                        return new IssueServiceConfigSubscriber(
                                "subversion.repository",
                                t.getName(),
                                null
                        );
                    }
                }
        );
    }
}
