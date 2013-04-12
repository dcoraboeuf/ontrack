package net.ontrack.extension.svn.service.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SVNRevisionPaths {

    private final SVNRevisionInfo info;
    private final List<SVNRevisionPath> paths = new ArrayList<>();

    public SVNRevisionPaths addPath(SVNRevisionPath path) {
        paths.add(path);
        return this;
    }

}
