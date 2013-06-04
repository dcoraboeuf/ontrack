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
import java.util.Iterator;

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

    @Test
    public void branch_low() {
        DefaultSVNExplorerService service = createService();
        // Change log summary (same)
        BranchSummary branch = new BranchSummary(1, "B1", "Branch 1", new ProjectSummary(1, "P1", "Project 1"));
        ChangeLogSummary summary = new ChangeLogSummary(
                "1",
                branch,
                new SVNBuild(
                        new BuildSummary(101, "1.0.1", "Build 1.0.1", branch),
                        new SVNHistory(ref("tags/11", 112))
                                .add(ref("tags/1.0.1", 111))
                                .add(ref("branches/1.0.x", 110))
                                .add(ref("tags/1.0.0", 101))
                                .add(ref("trunk", 100)),
                        Collections.<BuildValidationStamp>emptyList(),
                        Collections.<BuildPromotionLevel>emptyList()
                ),
                new SVNBuild(
                        new BuildSummary(9, "0.0.9", "Build 0.0.9", branch),
                        new SVNHistory(ref("tags/0.0.9", 91))
                                .add(ref("tags/0.0.9", 91))
                                .add(ref("trunk", 90)),
                        Collections.<BuildValidationStamp>emptyList(),
                        Collections.<BuildPromotionLevel>emptyList()
                )
        );
        // References
        Collection<ChangeLogReference> references = service.getChangeLogReferences(summary);
        // Check
        assertNotNull(references);
        assertEquals(2, references.size());
        Iterator<ChangeLogReference> iterator = references.iterator();
        ChangeLogReference reference0 = iterator.next();
        {
            assertEquals("/project/trunk", reference0.getPath());
            assertEquals(100, reference0.getStart());
            assertEquals(90, reference0.getEnd());
        }
        ChangeLogReference reference1 = iterator.next();
        {
            assertEquals("/project/branches/1.0.x", reference1.getPath());
            assertEquals(110, reference1.getStart());
            assertEquals(101, reference1.getEnd());
        }
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
