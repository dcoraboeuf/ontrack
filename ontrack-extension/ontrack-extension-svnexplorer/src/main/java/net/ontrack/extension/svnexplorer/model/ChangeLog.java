package net.ontrack.extension.svnexplorer.model;

import lombok.Data;

@Data
public class ChangeLog {

    private final ChangeLogSummary summary;
    private ChangeLogRevisions revisions;
}
