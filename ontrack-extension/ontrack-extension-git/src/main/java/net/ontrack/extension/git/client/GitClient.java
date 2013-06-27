package net.ontrack.extension.git.client;

import net.ontrack.extension.git.model.GitConfiguration;

import java.util.Collection;
import java.util.List;

public interface GitClient {

    Collection<GitTag> getTags();

    GitConfiguration getConfiguration();

    GitLog log(String from, String to);

    void sync();

    GitDiff diff(String from, String to);

    boolean isCommitDefined(String commit);
}
