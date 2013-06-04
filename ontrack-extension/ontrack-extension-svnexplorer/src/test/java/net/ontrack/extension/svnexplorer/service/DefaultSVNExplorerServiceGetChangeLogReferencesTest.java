package net.ontrack.extension.svnexplorer.service;

import net.ontrack.core.model.*;
import net.ontrack.extension.svn.service.model.SVNHistory;
import net.ontrack.extension.svn.service.model.SVNReference;
import net.ontrack.extension.svnexplorer.model.ChangeLogReference;
import net.ontrack.extension.svnexplorer.model.ChangeLogSummary;
import net.ontrack.extension.svnexplorer.model.SVNBuild;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.*;

public class DefaultSVNExplorerServiceGetChangeLogReferencesTest {

    @Test
    public void none() {
        DefaultSVNExplorerService service = createService();
        // Change log summary (same)
        BranchSummary branch = new BranchSummary(1, "B1", "Branch 1", new ProjectSummary(1, "P1", "Project 1"));
        SVNReference trunk = new SVNReference("/project/trunk", "http://server/project/trunk", 100000, new DateTime());
        ChangeLogSummary summary = new ChangeLogSummary(
                "1",
                branch,
                new SVNBuild(
                        new BuildSummary(1, "10", "Build 10", branch),
                        new SVNHistory(trunk)
                                .add(trunk),
                        Collections.<BuildValidationStamp>emptyList(),
                        Collections.<BuildPromotionLevel>emptyList()
                ),
                new SVNBuild(
                        new BuildSummary(1, "10", "Build 10", branch),
                        new SVNHistory(trunk)
                                .add(trunk),
                        Collections.<BuildValidationStamp>emptyList(),
                        Collections.<BuildPromotionLevel>emptyList()
                )
        );
        // References
        Collection<ChangeLogReference> references = service.getChangeLogReferences(summary);
        // Check
        assertNotNull(references);
        assertEquals(1, references.size());
        assertTrue(references.iterator().next().isNone());
    }

    @Test
    public void simple() {
        DefaultSVNExplorerService service = createService();
        // Change log summary (same)
        BranchSummary branch = new BranchSummary(1, "B1", "Branch 1", new ProjectSummary(1, "P1", "Project 1"));
        ChangeLogSummary summary = new ChangeLogSummary(
                "1",
                branch,
                new SVNBuild(
                        new BuildSummary(1, "11", "Build 11", branch),
                        new SVNHistory(ref("tags/11", 110001))
                                .add(ref("tags/11", 110001))
                                .add(ref("trunk", 110000)),
                        Collections.<BuildValidationStamp>emptyList(),
                        Collections.<BuildPromotionLevel>emptyList()
                ),
                new SVNBuild(
                        new BuildSummary(1, "10", "Build 10", branch),
                        new SVNHistory(ref("tags/10", 100001))
                                .add(ref("tags/10", 100001))
                                .add(ref("trunk", 100000)),
                        Collections.<BuildValidationStamp>emptyList(),
                        Collections.<BuildPromotionLevel>emptyList()
                )
        );
        // References
        Collection<ChangeLogReference> references = service.getChangeLogReferences(summary);
        // Check
        assertNotNull(references);
        assertEquals(1, references.size());
        ChangeLogReference reference = references.iterator().next();
        assertEquals("/project/trunk", reference.getPath());
        assertEquals(110000, reference.getStart());
        assertEquals(100000, reference.getEnd());
    }

    private SVNReference ref(String path, int revision) {
        return new SVNReference("/project/" + path, "http://server/project/" + path, revision, new DateTime());
    }

    private DefaultSVNExplorerService createService() {
        // No need for dependencies
        return new DefaultSVNExplorerService(
                null,
                null,
                null,
                null,
                null
        );
    }
}
