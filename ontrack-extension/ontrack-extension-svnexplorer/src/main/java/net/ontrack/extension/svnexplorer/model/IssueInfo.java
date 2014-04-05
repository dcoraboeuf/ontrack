package net.ontrack.extension.svnexplorer.model;

import lombok.Data;
import net.ontrack.extension.issue.Issue;
import net.ontrack.extension.svn.service.model.SVNRepository;

import java.util.List;

@Data
public class IssueInfo {

    private final SVNRepository repository;
    private final Issue issue;
    private final String formattedUpdateTime;
    private final RevisionInfo revisionInfo;
    private final List<RevisionInfo> mergedRevisionInfos;
    private final List<ChangeLogRevision> revisions;

}
