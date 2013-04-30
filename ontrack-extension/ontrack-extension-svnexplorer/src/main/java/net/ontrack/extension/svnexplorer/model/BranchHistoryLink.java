package net.ontrack.extension.svnexplorer.model;

import lombok.Data;
import net.ontrack.extension.svn.SVNEventType;
import org.joda.time.DateTime;

@Data
public class BranchHistoryLink {

    private final long revision;
    private final DateTime creation;
    private final SVNEventType type;
    private final BranchHistoryLine target;

}
