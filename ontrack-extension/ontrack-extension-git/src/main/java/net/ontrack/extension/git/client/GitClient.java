package net.ontrack.extension.git.client;

import net.ontrack.extension.git.model.GitConfiguration;

import java.util.Collection;

public interface GitClient {

    Collection<GitTag> getTags();

    GitConfiguration getConfiguration();

    void log(String from, String to);
}
