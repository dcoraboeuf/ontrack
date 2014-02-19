package net.ontrack.extension.svnexplorer.service;

import net.ontrack.core.model.*;
import net.ontrack.extension.svn.service.model.SVNHistory;
import net.ontrack.extension.svn.service.model.SVNReference;
import net.ontrack.extension.svn.service.model.SVNRepository;
import net.ontrack.extension.svnexplorer.model.ChangeLogReference;
import net.ontrack.extension.svnexplorer.model.ChangeLogSummary;
import net.ontrack.extension.svnexplorer.model.SVNBuild;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Arrays;
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
                mockSVNRepository(),
                new SVNBuild(
                        new BuildSummary(1, "10", "Build 10", branch),
                        new SVNHistory(trunk),
                        Collections.<BuildValidationStamp>emptyList(),
                        Collections.<BuildPromotionLevel>emptyList()
                ),
                new SVNBuild(
                        new BuildSummary(1, "10", "Build 10", branch),
                        new SVNHistory(trunk),
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
                mockSVNRepository(),
                new SVNBuild(
                        new BuildSummary(1, "11", "Build 11", branch),
                        new SVNHistory(Arrays.asList(
                                ref("trunk", 110000))),
                        Collections.<BuildValidationStamp>emptyList(),
                        Collections.<BuildPromotionLevel>emptyList()
                ),
                new SVNBuild(
                        new BuildSummary(1, "10", "Build 10", branch),
                        new SVNHistory(Arrays.asList(
                                ref("trunk", 100000))),
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
        assertEquals(100000, reference.getStart());
        assertEquals(110000, reference.getEnd());
    }

    @Test
    public void branch_low() {
        DefaultSVNExplorerService service = createService();
        // Change log summary (same)
        BranchSummary branch = new BranchSummary(1, "B1", "Branch 1", new ProjectSummary(1, "P1", "Project 1"));
        ChangeLogSummary summary = new ChangeLogSummary(
                "1",
                branch,
                mockSVNRepository(),
                new SVNBuild(
                        new BuildSummary(101, "1.0.1", "Build 1.0.1", branch),
                        new SVNHistory(Arrays.asList(
                                ref("branches/1.0.x", 110),
                                ref("trunk", 100))),
                        Collections.<BuildValidationStamp>emptyList(),
                        Collections.<BuildPromotionLevel>emptyList()
                ),
                new SVNBuild(
                        new BuildSummary(9, "0.0.9", "Build 0.0.9", branch),
                        new SVNHistory(Arrays.asList(
                                ref("trunk", 90))),
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
        ChangeLogReference referenceBranch = iterator.next();
        {
            assertEquals("/project/branches/1.0.x", referenceBranch.getPath());
            assertEquals(0, referenceBranch.getStart());
            assertEquals(110, referenceBranch.getEnd());
        }
        ChangeLogReference referenceTrunk = iterator.next();
        {
            assertEquals("/project/trunk", referenceTrunk.getPath());
            assertEquals(90, referenceTrunk.getStart());
            assertEquals(100, referenceTrunk.getEnd());
        }
    }

    private SVNRepository mockSVNRepository() {
        return new SVNRepository(
                0,
                "test",
                "http://test",
                "test",
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                1,
                null,
                null
        );
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
