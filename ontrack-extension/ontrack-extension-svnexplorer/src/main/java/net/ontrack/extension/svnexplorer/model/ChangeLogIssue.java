package net.ontrack.extension.svnexplorer.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.ontrack.extension.issue.Issue;
import net.ontrack.extension.svn.service.model.SVNRevisionInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangeLogIssue {

    private final Issue issue;
    private final String formattedUpdateTime;
    private final List<SVNRevisionInfo> revisions;

    public ChangeLogIssue(Issue issue, String formattedUpdateTime) {
        this(issue, formattedUpdateTime, Collections.<SVNRevisionInfo>emptyList());
    }

    public ChangeLogIssue addRevision(SVNRevisionInfo revision) {
        List<SVNRevisionInfo> list = new ArrayList<>(this.revisions);
        list.add(revision);
        return new ChangeLogIssue(issue, formattedUpdateTime, list);
    }
}
