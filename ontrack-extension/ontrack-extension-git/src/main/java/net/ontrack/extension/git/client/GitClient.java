package net.ontrack.extension.git.client;

import net.ontrack.extension.git.client.plot.GPlot;
import net.ontrack.extension.git.model.GitConfiguration;

import java.util.Collection;

public interface GitClient {

    Collection<GitTag> getTags();

    GitConfiguration getConfiguration();

    GPlot log(String from, String to);
}
