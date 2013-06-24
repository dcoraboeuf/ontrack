package net.ontrack.extension.github;

import net.ontrack.extension.api.support.ExtensionAdapter;
import net.ontrack.extension.git.GitExtension;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component
public class GitHubExtension extends ExtensionAdapter {

    public static final String EXTENSION = "github";

    public GitHubExtension() {
        super(EXTENSION);
    }

    @Override
    public Collection<String> getDependencies() {
        return Collections.singleton(GitExtension.EXTENSION);
    }
}
