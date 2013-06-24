package net.ontrack.extension.git.model;

import lombok.Data;

@Data
public class ChangeLog {

    private final ChangeLogSummary summary;
    private ChangeLogCommits commits;
    // private ChangeLogIssues issues;
    private ChangeLogFiles files;
    // private ChangeLogInfo info;
}
