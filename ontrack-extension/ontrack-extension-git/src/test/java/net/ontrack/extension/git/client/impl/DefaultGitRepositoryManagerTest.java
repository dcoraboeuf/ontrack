package net.ontrack.extension.git.client.impl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DefaultGitRepositoryManagerTest {

    @Test
    public void getRepositoryId_1() {
        assertEquals(
                "git___github_com_dcoraboeuf_ontrack_git",
                new DefaultGitRepositoryManager(null).getRepositoryId("git://github.com/dcoraboeuf/ontrack.git"));
    }

    @Test
    public void getRepositoryId_2() {
        assertEquals(
                "git_github_com_dcoraboeuf_ontrack_git",
                new DefaultGitRepositoryManager(null).getRepositoryId("git@github.com:dcoraboeuf/ontrack.git"));
    }

    @Test
    public void getRepositoryId_3() {
        assertEquals(
                "https___github_com_dcoraboeuf_ontrack_git",
                new DefaultGitRepositoryManager(null).getRepositoryId("https://github.com/dcoraboeuf/ontrack.git"));
    }

}
