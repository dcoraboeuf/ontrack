package net.ontrack.extension.github.service;

import net.ontrack.core.support.InputException;

public class GitHubIssueNotFoundException extends InputException {
    public GitHubIssueNotFoundException(String project, int issueKey) {
        super(project, issueKey);
    }
}
