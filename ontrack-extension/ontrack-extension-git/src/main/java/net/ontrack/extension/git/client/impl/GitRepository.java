package net.ontrack.extension.git.client.impl;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;

public interface GitRepository {

    File wd();

    String getRemote();

    String getId();

    GitRepository sync() throws GitAPIException;

    Git git();
}
