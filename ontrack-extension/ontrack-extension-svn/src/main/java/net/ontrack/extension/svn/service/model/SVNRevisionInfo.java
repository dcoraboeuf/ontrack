package net.ontrack.extension.svn.service.model;

import lombok.Data;

@Data
public class SVNRevisionInfo {

    private final long revision;
    private final String author;
    private final String isoDateTime;
    private final String message;
    private final String revisionUrl;

}
