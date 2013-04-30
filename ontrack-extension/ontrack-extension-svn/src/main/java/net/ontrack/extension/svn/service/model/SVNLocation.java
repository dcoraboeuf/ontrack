package net.ontrack.extension.svn.service.model;

import lombok.Data;

@Data
public class SVNLocation {

    private final String path;
    private final long revision;

    public SVNLocation withRevision(long revision) {
        return new SVNLocation(path, revision);
    }
}
