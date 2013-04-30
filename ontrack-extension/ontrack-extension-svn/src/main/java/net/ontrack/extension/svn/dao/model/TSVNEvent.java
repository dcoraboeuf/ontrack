package net.ontrack.extension.svn.dao.model;

import lombok.Data;
import net.ontrack.extension.svn.SVNEventType;
import org.joda.time.DateTime;

@Data
public class TSVNEvent {

    private final DateTime creation;
    private final long revision;
    private final SVNEventType type;
    private final String copyFromPath;
    private final long copyFromRevision;
    private final String copyToPath;

}
