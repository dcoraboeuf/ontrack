package net.ontrack.extension.jira;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JIRAConfigurationExtensionTest {

    @Test
    public void isIssue_no_exclusion_no_match() {
        JIRAConfigurationExtension extension = new JIRAConfigurationExtension();
        // extension.configure(JIRAConfigurationExtension.EXCLUSIONS, "");
        assertFalse(extension.isIssue("TEST"));
    }

    @Test
    public void isIssue_no_exclusion_match() {
        JIRAConfigurationExtension extension = new JIRAConfigurationExtension();
        // extension.configure(JIRAConfigurationExtension.EXCLUSIONS, "");
        assertTrue(extension.isIssue("TEST-12"));
    }

    @Test
    public void isIssue_exclusion_no_match() {
        JIRAConfigurationExtension extension = new JIRAConfigurationExtension();
        extension.configure(JIRAConfigurationExtension.EXCLUSIONS, "TEST,PRJ-12");
        assertFalse(extension.isIssue("TST"));
    }

    @Test
    public void isIssue_exclusion_match_excluded_by_project() {
        JIRAConfigurationExtension extension = new JIRAConfigurationExtension();
        extension.configure(JIRAConfigurationExtension.EXCLUSIONS, "TEST,PRJ-12");
        assertFalse(extension.isIssue("TEST-12"));
    }

    @Test
    public void isIssue_exclusion_match_excluded_by_issue() {
        JIRAConfigurationExtension extension = new JIRAConfigurationExtension();
        extension.configure(JIRAConfigurationExtension.EXCLUSIONS, "TEST,PRJ-12");
        assertFalse(extension.isIssue("PRJ-12"));
    }

    @Test
    public void isIssue_no_exclusion_match_not_excluded() {
        JIRAConfigurationExtension extension = new JIRAConfigurationExtension();
        extension.configure(JIRAConfigurationExtension.EXCLUSIONS, "TEST,PRJ-12");
        assertTrue(extension.isIssue("PRJ-13"));
    }

}
