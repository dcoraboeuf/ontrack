package net.ontrack.extension.svn.service.model;

import lombok.Data;
import net.ontrack.core.model.ProjectSummary;

import java.util.Collection;

/**
 * Information that describes the dependencies of a {@link SVNRepository} when
 * it has to be deleted.
 */
@Data
public class SVNRepositoryDeletion {

    /**
     * Associated repository
     */
    private final SVNRepository repository;

    /**
     * Associated projects
     */
    private final Collection<ProjectSummary> projects;

}
