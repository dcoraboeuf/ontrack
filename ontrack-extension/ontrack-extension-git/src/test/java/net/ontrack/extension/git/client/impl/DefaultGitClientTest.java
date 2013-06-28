package net.ontrack.extension.git.client.impl;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.ontrack.extension.git.GitCommitNotFoundException;
import net.ontrack.extension.git.client.GitCommit;
import net.ontrack.extension.git.model.GitConfiguration;
import net.ontrack.service.EnvironmentService;
import net.ontrack.service.support.DirEnvironmentService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class DefaultGitClientTest {


    private static DefaultGitClient client;

    @BeforeClass
    public static void init() {
        String remote = "https://github.com/dcoraboeuf/ontrack.git";
        String branch = "master";
        EnvironmentService environmentService = new DirEnvironmentService(new File("target/work/git"));
        GitRepositoryManager repositoryManager = new DefaultGitRepositoryManager(environmentService);
        client = new DefaultGitClient(
                repositoryManager.getRepository(remote, branch),
                GitConfiguration.empty()
                        .withBranch(branch)
                        .withRemote(remote)
                        .withDefaults()
        );
        // Sync
        client.sync();
    }

    @Test(expected = GitCommitNotFoundException.class)
    public void getEarliestTagForCommit_commit_not_found() {
        client.getEarliestTagForCommit("xxx", Predicates.<String>alwaysTrue());
    }

    @Test
    public void getEarliestTagForCommit_exact_match() {
        String tag = client.getEarliestTagForCommit("44eab8b9a6eb16163227ad7d25dec53e73659ef4", Predicates.<String>alwaysTrue());
        assertEquals("ontrack-1.17", tag);
    }

    @Test
    public void getEarliestTagForCommit_earlier_match() {
        String tag = client.getEarliestTagForCommit("16baf53233f5b4013e70f777815f0050681dce67", Predicates.<String>alwaysTrue());
        assertEquals("ontrack-1.17", tag);
    }

    @Test
    public void getEarliestTagForCommit_no_match() {
        String tag = client.getEarliestTagForCommit("0fabc7d13fce41799cfb698f9fc5ed5a26854862", new Predicate<String>() {
            @Override
            public boolean apply(String tagName) {
                return tagName.startsWith("ontrack-1.1");
            }
        });
        assertNull(tag);
    }

    @Test
    public void getCommitFor() {
        String id = "a661c5f8c38fc461228423cbc2484da722130d8b";
        GitCommit commit = client.getCommitFor(id);
        assertNotNull(commit);
        assertEquals(id, commit.getId());
        assertEquals("#175 Jenkins decorations", commit.getShortMessage());
        assertEquals("#175 Jenkins decorations\n", commit.getFullMessage());
        assertEquals(new DateTime(2013, 6, 12, 7, 53, 25, DateTimeZone.UTC), commit.getCommitTime());
        assertNotNull(commit.getAuthor());
        assertEquals("Damien Coraboeuf", commit.getAuthor().getName());
    }

}
