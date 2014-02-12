package net.ontrack.extension.svn.dao;

import net.ontrack.core.model.Ack;
import net.ontrack.extension.svn.service.model.SVNRepository;
import net.ontrack.extension.svn.service.model.SVNRepositoryForm;

import java.util.List;

public interface RepositoryDao {

    List<SVNRepository> findAll();

    SVNRepository create(SVNRepositoryForm form);

    SVNRepository update(int id, SVNRepositoryForm form);

    SVNRepository getById(int id);

    Ack delete(int id);

}
