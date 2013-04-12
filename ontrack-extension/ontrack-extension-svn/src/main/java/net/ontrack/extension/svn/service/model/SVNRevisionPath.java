package net.ontrack.extension.svn.service.model;

import lombok.Data;

@Data
public class SVNRevisionPath {

    private final String path;
    private final String changeType;

}
