package net.ontrack.extension.svnexplorer.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.ontrack.extension.issue.IssueStatus;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangeLogInfoStatus {

    private final IssueStatus status;
    private int count;

    public ChangeLogInfoStatus(IssueStatus status) {
        this(status, 1);
    }

    public ChangeLogInfoStatus incr() {
        count++;
        return this;
    }
}
