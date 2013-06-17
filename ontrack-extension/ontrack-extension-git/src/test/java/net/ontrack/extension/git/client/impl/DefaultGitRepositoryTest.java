package net.ontrack.extension.git.client.impl;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DefaultGitRepositoryTest {

    @Test
    public void sync_clone() throws IOException, GitAPIException {
        DefaultGitRepository repository = createRepo(
                "git://github.com/dcoraboeuf/ontrack.git",
                "sync_clone");
        GitRepository repo = repository.sync();
        assertNotNull(repo);
        assertTrue(".git created", new File(repository.wd(), ".git").exists());
    }

    private DefaultGitRepository createRepo(String remote, String id) throws IOException {
        File wd = new File("target/work/git/" + id);
        FileUtils.deleteQuietly(wd);
        FileUtils.forceMkdir(wd);
        return new DefaultGitRepository(
                wd,
                remote,
                id
        );
    }

}
