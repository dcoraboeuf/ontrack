package net.ontrack.extension.svn.dao.model;

import lombok.Data;

@Data
public class TSVNCopyEvent {

    private final long revision;
    private final String copyFromPath;
    private final long copyFromRevision;
    private final String copyToPath;

}
