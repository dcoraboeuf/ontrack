package net.ontrack.extension.git;

import net.ontrack.extension.api.property.PropertyExtensionDescriptor;
import net.ontrack.extension.api.support.ExtensionAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class GitExtension extends ExtensionAdapter {

    public static final String EXTENSION = "git";
    private final GitRemoteProperty gitRemoteProperty;
    private final GitBranchProperty gitBranchProperty;
    private final GitTagProperty gitTagProperty;

    @Autowired
    public GitExtension(GitRemoteProperty gitRemoteProperty, GitBranchProperty gitBranchProperty, GitTagProperty gitTagProperty) {
        super(EXTENSION);
        this.gitRemoteProperty = gitRemoteProperty;
        this.gitBranchProperty = gitBranchProperty;
        this.gitTagProperty = gitTagProperty;
    }

    @Override
    public List<? extends PropertyExtensionDescriptor> getPropertyExtensionDescriptors() {
        return Arrays.asList(
                gitRemoteProperty,
                gitBranchProperty,
                gitTagProperty
        );
    }
}
