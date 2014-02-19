package net.ontrack.extension.svn.service;

import net.ontrack.core.model.Ack;
import net.ontrack.extension.svn.service.model.SVNRepository;
import net.ontrack.extension.svn.service.model.SVNRepositoryDeletion;
import net.ontrack.extension.svn.service.model.SVNRepositoryForm;

import java.util.List;

/**
 * Accessing to the SVN repositories
 */
public interface RepositoryService {

    /**
     * Gets the list of defined repository configurations
     */
    List<SVNRepository> getAllRepositories();

    /**
     * Creates a new repository configuration
     */
    SVNRepository createRepository(SVNRepositoryForm form);

    /**
     * Updates a repository configuration
     */
    SVNRepository updateRepository(int id, SVNRepositoryForm form);

    /**
     * Gets a repository configuration using its ID.
     */
    SVNRepository getRepository(int id);

    /**
     * Deletes a repository configuration.
     */
    Ack deleteRepository(int id);

    /**
     * Gets the protected password for a repository.
     * <p/>
     * This method is called internally using admin callbacks.
     *
     * @see net.ontrack.core.security.SecurityUtils#asAdmin(java.util.concurrent.Callable)
     */
    String getPassword(int id);

    /**
     * Gets the dependencies of a {@link SVNRepository} before its deletion.
     *
     * @param id ID of the repository
     * @return Repository and its dependencies
     */
    SVNRepositoryDeletion getConfigurationForDeletion(int id);
}
