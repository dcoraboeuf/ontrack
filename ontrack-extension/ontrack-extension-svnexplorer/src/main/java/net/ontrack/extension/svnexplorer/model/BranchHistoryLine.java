package net.ontrack.extension.svnexplorer.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.ontrack.core.model.BranchLastStatus;
import net.ontrack.extension.svn.service.model.SVNReference;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BranchHistoryLine {

    private final SVNReference current;
    private final boolean tag;
    private final BranchLastStatus lastStatus;
    private final List<BranchHistoryLine> lines;

    public BranchHistoryLine(SVNReference current, boolean tag) {
        this(current, tag, null, new ArrayList<BranchHistoryLine>());
    }

    public void addLine(BranchHistoryLine line) {
        lines.add(line);
    }

    public BranchHistoryLine withBranchLastStatus(BranchLastStatus lastStatus) {
        return new BranchHistoryLine(current, tag, lastStatus, lines);
    }

}
