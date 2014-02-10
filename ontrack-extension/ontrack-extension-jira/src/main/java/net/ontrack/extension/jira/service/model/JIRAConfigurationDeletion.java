package net.ontrack.extension.jira.service.model;

import lombok.Data;
import net.ontrack.core.model.ProjectSummary;

import java.util.List;

@Data
public class JIRAConfigurationDeletion {

    private final JIRAConfiguration configuration;
    /**
     * List of projects that have a JIRA property associated with them.
     */
    private final List<ProjectSummary> projects;

}
