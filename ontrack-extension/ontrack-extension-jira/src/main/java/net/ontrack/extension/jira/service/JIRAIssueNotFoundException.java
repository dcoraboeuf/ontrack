package net.ontrack.extension.jira.service;

import net.ontrack.core.support.InputException;

public class JIRAIssueNotFoundException extends InputException {

    public JIRAIssueNotFoundException(String issue) {
        super(issue);
    }
}
