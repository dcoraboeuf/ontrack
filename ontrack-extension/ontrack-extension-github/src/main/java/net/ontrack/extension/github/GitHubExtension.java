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
    public static final String GITHUB_ISSUE_PATTERN = "(#\\d+)";
    private final GitHubProjectProperty gitHubProjectProperty;
    private final GitHubAuthenticationProperty gitHubAuthenticationProperty;

    @Autowired
    public GitHubExtension(GitHubProjectProperty gitHubProjectProperty, GitHubAuthenticationProperty gitHubAuthenticationProperty) {
        super(EXTENSION);
        this.gitHubProjectProperty = gitHubProjectProperty;
        this.gitHubAuthenticationProperty = gitHubAuthenticationProperty;
    }

    public static String getIssueUrl(String project, String id) {
        return String.format("https://github.com/%s/issues/%s", project, id);
    }

    @Override
    public List<? extends PropertyExtensionDescriptor> getPropertyExtensionDescriptors() {
        return Arrays.asList(
                gitHubProjectProperty,
                gitHubAuthenticationProperty
        );
    }

    @Override
    public Collection<String> getDependencies() {
        return Collections.singleton(GitExtension.EXTENSION);
    }
}
