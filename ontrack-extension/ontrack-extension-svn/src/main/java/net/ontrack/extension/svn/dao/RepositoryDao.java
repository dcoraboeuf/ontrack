package net.ontrack.extension.svn.dao;

import net.ontrack.core.model.Ack;
import net.ontrack.extension.svn.dao.model.TRepository;
import net.ontrack.extension.svn.service.model.SVNRepositoryForm;

import java.util.Collection;
import java.util.List;

public interface RepositoryDao {

    List<TRepository> findAll();

    TRepository create(SVNRepositoryForm form);

    TRepository update(int id, SVNRepositoryForm form);

    TRepository getById(int id);

    Ack delete(int id);

    String getPassword(int id);

    Collection<TRepository> findByIssueServiceConfig(String serviceId, int configId);
}
