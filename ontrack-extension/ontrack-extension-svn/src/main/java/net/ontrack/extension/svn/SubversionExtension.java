package net.ontrack.extension.svn;

import net.ontrack.extension.api.action.TopActionExtension;
import net.ontrack.extension.api.configuration.ConfigurationExtension;
import net.ontrack.extension.api.property.PropertyExtensionDescriptor;
import net.ontrack.extension.api.support.ExtensionAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
public class SubversionExtension extends ExtensionAdapter {

    public static final String EXTENSION = "svn";
    public static final String SUBVERSION_BUILD_PATH = "buildPath";
    private final SubversionPathPropertyExtension subversionPathPropertyExtension;
    private final SubversionBuildPathPropertyExtension subversionBuildPathPropertyExtension;
    private final SubversionConfigurationExtension subversionConfigurationExtension;
    private final IndexationConfigurationExtension indexationConfigurationExtension;
    private final IndexationActionController indexationActionController;
    private final SVNConfigurationController svnConfigurationController;

    @Autowired
    public SubversionExtension(
            SubversionPathPropertyExtension subversionPathPropertyExtension,
            SubversionBuildPathPropertyExtension subversionBuildPathPropertyExtension,
            SubversionConfigurationExtension subversionConfigurationExtension,
            IndexationConfigurationExtension indexationConfigurationExtension,
            IndexationActionController indexationActionController, SVNConfigurationController svnConfigurationController) {
        super(EXTENSION);
        this.subversionPathPropertyExtension = subversionPathPropertyExtension;
        this.subversionBuildPathPropertyExtension = subversionBuildPathPropertyExtension;
        this.subversionConfigurationExtension = subversionConfigurationExtension;
        this.indexationConfigurationExtension = indexationConfigurationExtension;
        this.indexationActionController = indexationActionController;
        this.svnConfigurationController = svnConfigurationController;
    }

    @Override
    public List<? extends PropertyExtensionDescriptor> getPropertyExtensionDescriptors() {
        return Arrays.asList(subversionPathPropertyExtension, subversionBuildPathPropertyExtension);
    }

    @Override
    public List<? extends ConfigurationExtension> getConfigurationExtensions() {
        return Arrays.asList(subversionConfigurationExtension, indexationConfigurationExtension);
    }

    @Override
    public Collection<? extends TopActionExtension> getTopLevelActions() {
        return Arrays.asList(
                indexationActionController,
                svnConfigurationController);
    }
}
