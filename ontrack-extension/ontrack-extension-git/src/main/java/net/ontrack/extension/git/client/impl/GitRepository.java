package net.ontrack.extension.git.client.impl;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;

public interface GitRepository {

    File wd();

    String getRemote();

    String getBranch();

    String getId();

    void sync() throws GitAPIException;

    Git git();

    RevCommit getCommitForTag(ObjectId tag);
}
