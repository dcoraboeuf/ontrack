package net.ontrack.extension.svnexplorer.model;

import lombok.Data;
import net.ontrack.extension.svn.SVNEventType;

@Data
public class BranchHistoryLink {

    private final long revision;
    private final SVNEventType type;
    private final BranchHistoryLine target;

}
