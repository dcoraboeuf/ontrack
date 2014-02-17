package net.ontrack.extension.svn;

import net.ontrack.extension.api.action.TopActionExtension;
import net.ontrack.extension.api.property.PropertyExtensionDescriptor;
import net.ontrack.extension.api.support.ExtensionAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

// FIXME Migration code
@Component
public class SubversionExtension extends ExtensionAdapter {

    public static final String EXTENSION = "svn";
    public static final String SUBVERSION_BUILD_PATH = "buildPath";
    private final SubversionRepositoryPropertyExtension subversionRepositoryPropertyExtension;
    private final SubversionPathPropertyExtension subversionPathPropertyExtension;
    private final SubversionBuildPathPropertyExtension subversionBuildPathPropertyExtension;
    private final SVNConfigurationController svnConfigurationController;

    @Autowired
    public SubversionExtension(
            SubversionRepositoryPropertyExtension subversionRepositoryPropertyExtension, SubversionPathPropertyExtension subversionPathPropertyExtension,
            SubversionBuildPathPropertyExtension subversionBuildPathPropertyExtension,
            SVNConfigurationController svnConfigurationController) {
        super(EXTENSION);
        this.subversionRepositoryPropertyExtension = subversionRepositoryPropertyExtension;
        this.subversionPathPropertyExtension = subversionPathPropertyExtension;
        this.subversionBuildPathPropertyExtension = subversionBuildPathPropertyExtension;
        this.svnConfigurationController = svnConfigurationController;
    }

    @Override
    public List<? extends PropertyExtensionDescriptor> getPropertyExtensionDescriptors() {
        return Arrays.asList(subversionRepositoryPropertyExtension, subversionPathPropertyExtension, subversionBuildPathPropertyExtension);
    }

    @Override
    public Collection<? extends TopActionExtension> getTopLevelActions() {
        return Arrays.asList(svnConfigurationController);
    }
}
