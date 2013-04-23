package net.ontrack.extension.svnexplorer.service;

import net.ontrack.extension.svn.service.model.SVNLocation;
import org.junit.Test;

import static net.ontrack.extension.svnexplorer.service.SVNExplorerPathUtils.followsBuildPattern;
import static net.ontrack.extension.svnexplorer.service.SVNExplorerPathUtils.getBuildName;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SVNExplorerPathUtilsTest {

    @Test
    public void followsBuildPattern_tag_prefix_ok() {
        assertTrue(followsBuildPattern(new SVNLocation("/project/tags/2.7.0.1", 100000L), "/project/tags/2.7.*"));
    }

    @Test
    public void followsBuildPattern_tag_prefix_nok() {
        assertFalse(followsBuildPattern(new SVNLocation("/project/tags/2.6.0.1", 100000L), "/project/tags/2.7.*"));
    }

    @Test
    public void followsBuildPattern_tag_ok() {
        assertTrue(followsBuildPattern(new SVNLocation("/project/tags/2.7.0.1", 100000L), "/project/tags/*"));
    }

    @Test
    public void followsBuildPattern_tag_nok() {
        assertFalse(followsBuildPattern(new SVNLocation("/project/branches/2.7.x", 100000L), "/project/tags/*"));
    }

    @Test
    public void followsBuildPattern_revision_ok() {
        assertTrue(followsBuildPattern(new SVNLocation("/project/trunk", 100000L), "/project/trunk@*"));
    }

    @Test
    public void followsBuildPattern_revision_nok() {
        assertFalse(followsBuildPattern(new SVNLocation("/project/tags/2.7.0.1", 100000L), "/project/trunk@*"));
    }

    @Test
    public void getBuildName_tag_prefix() {
        assertEquals("2.7.0.1", getBuildName(new SVNLocation("/project/tags/2.7.0.1", 100000L), "/project/tags/2.7.*"));
    }

    @Test(expected = IllegalStateException.class)
    public void getBuildName_tag_prefix_nok() {
        getBuildName(new SVNLocation("/project/tags/2.6.0.1", 100000L), "/project/tags/2.7.*");
    }

    @Test
    public void getBuildName_tag() {
        assertEquals("2.7.0.1", getBuildName(new SVNLocation("/project/tags/2.7.0.1", 100000L), "/project/tags/*"));
    }

    @Test
    public void getBuildName_revision() {
        assertEquals("123456", getBuildName(new SVNLocation("/project/trunk", 123456), "/project/trunk@*"));
    }

    @Test(expected = IllegalStateException.class)
    public void getBuildName_revision_nok() {
        getBuildName(new SVNLocation("/project/branches/2.6.x", 123456), "/project/trunk@*");
    }

}
