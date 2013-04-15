package net.ontrack.extension.svnexplorer.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.ontrack.extension.jira.service.model.JIRAStatus;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangeLogInfoStatus {

    private final JIRAStatus status;
    private int count;

    public ChangeLogInfoStatus(JIRAStatus status) {
        this(status, 1);
    }

    public ChangeLogInfoStatus incr() {
        count++;
        return this;
    }
}
