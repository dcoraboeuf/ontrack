package net.ontrack.extension.jira.service.model;

import lombok.Data;
import net.ontrack.extension.issue.IssueStatus;

@Data
public class JIRAStatus implements IssueStatus {

    private final String name;
    private final String iconUrl;

}
