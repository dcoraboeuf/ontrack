package net.ontrack.extension.svn.service.model;

import lombok.Data;
import org.joda.time.DateTime;

@Data
public class SVNRevisionInfo {

    private final long revision;
    private final String author;
    private final DateTime dateTime;
    private final String path;
    private final String isoDateTime;
    private final String message;
    private final String revisionUrl;

    public SVNLocation toLocation () {
        return new SVNLocation(path, revision);
    }

}
