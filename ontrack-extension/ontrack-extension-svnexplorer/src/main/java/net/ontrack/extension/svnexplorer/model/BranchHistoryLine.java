package net.ontrack.extension.svnexplorer.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.model.BuildSummary;
import net.ontrack.core.model.Promotion;
import net.ontrack.extension.svn.service.model.SVNReference;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BranchHistoryLine {

    private final SVNReference current;
    private final SVNReference creation;
    private final boolean tag;
    private final BranchSummary branch;
    private final BuildSummary latestBuild;
    private final List<Promotion> promotions;
    private final List<BranchHistoryLink> links;

    public BranchHistoryLine(SVNReference current) {
        this(current, null, false, null, null, null, new ArrayList<BranchHistoryLink>());
    }

    public void addLink(BranchHistoryLink link) {
        links.add(link);
    }
}
