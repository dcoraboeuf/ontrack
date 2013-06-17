package net.ontrack.extension.git.client;

import java.util.Collection;

public interface GitClient {

    Collection<GitTag> getTags();

}
