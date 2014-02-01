package net.ontrack.extension.git.jira;

import net.ontrack.core.model.BranchSummary;

import java.util.Collection;
import java.util.Locale;

public interface GitJiraService {

    Collection<BranchSummary> getBranchesWithIssue(String key);

    GitJiraIssueInfo getIssueInfo(Locale locale, int branchId, String key);
}
