package net.ontrack.extension.svn.service.model;

import lombok.Data;
import org.joda.time.DateTime;

@Data
public class SVNReference {

    private final String path;
    private final String url;
    private final long revision;
    private final DateTime time;

    public SVNLocation toLocation() {
        return new SVNLocation(path, revision);
    }
}
