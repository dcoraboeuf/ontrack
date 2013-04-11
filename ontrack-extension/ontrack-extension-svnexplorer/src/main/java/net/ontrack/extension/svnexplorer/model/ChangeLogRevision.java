package net.ontrack.extension.svnexplorer.model;

import lombok.Data;

@Data
public class ChangeLogRevision {

    private final int level;
    private final long revision;
    private final String author;
    private final String isoDateTime;
    private final String message;
    private final String revisionUrl;

}
