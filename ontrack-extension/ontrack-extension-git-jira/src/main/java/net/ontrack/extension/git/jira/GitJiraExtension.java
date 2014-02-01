package net.ontrack.extension.git.jira;

import net.ontrack.extension.api.support.ExtensionAdapter;
import net.ontrack.extension.git.GitExtension;
import net.ontrack.extension.jira.JIRAExtension;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

@Component
public class GitJiraExtension extends ExtensionAdapter {

    public static final String EXTENSION = "git-jira";

    public GitJiraExtension() {
        super(EXTENSION);
    }

    @Override
    public Collection<String> getDependencies() {
        return Arrays.asList(
                GitExtension.EXTENSION,
                JIRAExtension.EXTENSION
        );
    }
}
