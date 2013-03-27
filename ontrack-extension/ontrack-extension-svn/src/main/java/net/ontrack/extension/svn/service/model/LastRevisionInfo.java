package net.ontrack.extension.svn.service.model;

import lombok.Data;

@Data
public class LastRevisionInfo {

    private final long revision;
    private final String message;
    private final long repositoryRevision;

}
