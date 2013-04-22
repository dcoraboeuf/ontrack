package net.ontrack.extension.svnexplorer.model;

import lombok.Data;
import net.ontrack.extension.jira.service.model.JIRAIssue;

import java.util.List;

@Data
public class IssueInfo {

    private final JIRAIssue issue;
    private final String formattedUpdateTime;
    private final RevisionInfo revisionInfo;
    private final List<ChangeLogRevision> revisions;

}
