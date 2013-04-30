package net.ontrack.extension.svn.service.model;

import lombok.Data;
import net.ontrack.extension.svn.SVNEventType;

@Data
public class EventSVN {

    private final long revision;
    private final SVNEventType type;
    private final String copyFromPath;
    private final long copyFromRevision;
    private final String copyToPath;

}
