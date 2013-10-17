package net.ontrack.extension.git.client.impl;

import net.ontrack.extension.git.GitCommitNotFoundException;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;

public class DefaultGitRepository implements GitRepository {

    private final Logger logger = LoggerFactory.getLogger(GitRepository.class);
    private final File wd;
    private final String remote;
    private final String branch;
    private final String id;
    private Git git;

    public DefaultGitRepository(File wd, String remote, String branch, String id) {
        this.wd = wd;
        this.remote = remote;
        this.branch = branch;
        this.id = id;
    }

    @Override
    public File wd() {
        return wd;
    }

    @Override
    public String getRemote() {
        return remote;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getBranch() {
        return branch;
    }

    @Override
    public synchronized void sync() throws GitAPIException {
        // Clone or update?
        if (new File(wd, ".git").exists()) {
            // Fetch
            fetch();
        } else {
            // Clone
            cloneRemote();
        }
    }

    @Override
    public synchronized Git git() {
        if (git == null) {
            // Gets the repository
            FileRepository repository;
            try {
                repository = new FileRepositoryBuilder()
                        .setWorkTree(wd)
                        .build();
            } catch (IOException e) {
                throw new GitIOException(e);
            }
            // Gets the Git
            git = new Git(repository);
        }
        return git;
    }

    @Override
    public RevCommit getCommitForTag(ObjectId tag) {
        try {
            Iterator<RevCommit> commits = git.log().add(tag).setMaxCount(1).call().iterator();
            if (commits.hasNext()) {
                return commits.next();
            } else {
                throw new GitCommitNotFoundException(tag.getName());
            }
        } catch (GitAPIException e) {
            throw new GitException(e);
        } catch (IOException e) {
            throw new GitIOException(e);
        }
    }

    protected synchronized void cloneRemote() throws GitAPIException {
        logger.debug("[git] Cloning {} into {}", remote, wd);
        new CloneCommand()
                .setDirectory(wd)
                .setURI(remote)
                .setBranchesToClone(Collections.singleton(branch))
                .call();
        // Check
        if (!new File(wd, ".git").exists()) {
            throw new GitCannotCloneException(wd);
        }
        // Done
        logger.debug("[git] Clone done for {}", remote);
    }

    protected synchronized void fetch() throws GitAPIException {
        logger.debug("[git] Fetching {} into {}", remote, wd);
        git().fetch().call();
        logger.debug("[git] Fetching done for {}", remote);
    }

}
