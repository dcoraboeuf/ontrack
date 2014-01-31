package net.ontrack.extension.git.jira;

import net.ontrack.core.model.BranchSummary;

import java.util.Collection;

public interface GitJiraService {

    Collection<BranchSummary> getBranchesWithIssue(String key);

}
