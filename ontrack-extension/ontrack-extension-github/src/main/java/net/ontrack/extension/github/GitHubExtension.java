package net.ontrack.extension.github;

import net.ontrack.extension.api.property.PropertyExtensionDescriptor;
import net.ontrack.extension.api.support.ExtensionAdapter;
import net.ontrack.extension.git.GitExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class GitHubExtension extends ExtensionAdapter {

    public static final String EXTENSION = "github";
    private final GitHubProjectProperty gitHubProjectProperty;

    @Autowired
    public GitHubExtension(GitHubProjectProperty gitHubProjectProperty) {
        super(EXTENSION);
        this.gitHubProjectProperty = gitHubProjectProperty;
    }

    @Override
    public List<? extends PropertyExtensionDescriptor> getPropertyExtensionDescriptors() {
        return Arrays.asList(
                gitHubProjectProperty
        );
    }

    @Override
    public Collection<String> getDependencies() {
        return Collections.singleton(GitExtension.EXTENSION);
    }
}
