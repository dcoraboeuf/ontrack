package net.ontrack.extension.git;

import net.ontrack.extension.api.action.ActionExtension;
import net.ontrack.extension.api.action.EntityActionExtension;
import net.ontrack.extension.api.property.PropertyExtensionDescriptor;
import net.ontrack.extension.api.support.ExtensionAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class GitExtension extends ExtensionAdapter {

    public static final String EXTENSION = "git";
    private final GitRemoteProperty gitRemoteProperty;
    private final GitCommitLinkProperty gitCommitLinkProperty;
    private final GitFileAtCommitLinkProperty gitFileAtCommitLinkProperty;
    private final GitBranchProperty gitBranchProperty;
    private final GitTagProperty gitTagProperty;
    private final GitChangeLogAction gitChangeLogAction;
    private final GitImportBuildsAction gitImportBuildsAction;

    @Autowired
    public GitExtension(GitRemoteProperty gitRemoteProperty, GitCommitLinkProperty gitCommitLinkProperty, GitFileAtCommitLinkProperty gitFileAtCommitLinkProperty, GitBranchProperty gitBranchProperty, GitTagProperty gitTagProperty,
                        GitChangeLogAction gitChangeLogAction,
                        GitImportBuildsAction gitImportBuildsAction) {
        super(EXTENSION);
        this.gitRemoteProperty = gitRemoteProperty;
        this.gitCommitLinkProperty = gitCommitLinkProperty;
        this.gitFileAtCommitLinkProperty = gitFileAtCommitLinkProperty;
        this.gitBranchProperty = gitBranchProperty;
        this.gitTagProperty = gitTagProperty;
        this.gitChangeLogAction = gitChangeLogAction;
        this.gitImportBuildsAction = gitImportBuildsAction;
    }

    @Override
    public Collection<? extends EntityActionExtension> getEntityActions() {
        return Collections.singleton(gitImportBuildsAction);
    }

    @Override
    public Collection<? extends ActionExtension> getDiffActions() {
        return Collections.singleton(gitChangeLogAction);
    }

    @Override
    public List<? extends PropertyExtensionDescriptor> getPropertyExtensionDescriptors() {
        return Arrays.asList(
                gitRemoteProperty,
                gitCommitLinkProperty,
                gitFileAtCommitLinkProperty,
                gitBranchProperty,
                gitTagProperty
        );
    }
}
