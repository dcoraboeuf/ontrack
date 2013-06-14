package net.ontrack.extension.svnexplorer;

import net.ontrack.extension.api.action.ActionExtension;
import net.ontrack.extension.api.action.EntityActionExtension;
import net.ontrack.extension.api.property.PropertyExtensionDescriptor;
import net.ontrack.extension.api.support.ExtensionAdapter;
import net.ontrack.extension.jira.JIRAExtension;
import net.ontrack.extension.svn.SubversionExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class SVNExplorerExtension extends ExtensionAdapter {

    public static final String EXTENSION = "svnexplorer";
    private final ChangeLogActionController changeLogActionController;
    private final SensibleFilesPropertyExtension sensibleFilesPropertyExtension;
    private final BranchHistoryActionController branchHistoryActionController;
    private final ProjectRootPathPropertyExtension projectRootPathPropertyExtension;

    @Autowired
    public SVNExplorerExtension(
            ChangeLogActionController changeLogActionController,
            SensibleFilesPropertyExtension sensibleFilesPropertyExtension, BranchHistoryActionController branchHistoryActionController, ProjectRootPathPropertyExtension projectRootPathPropertyExtension) {
        super(EXTENSION);
        this.changeLogActionController = changeLogActionController;
        this.sensibleFilesPropertyExtension = sensibleFilesPropertyExtension;
        this.branchHistoryActionController = branchHistoryActionController;
        this.projectRootPathPropertyExtension = projectRootPathPropertyExtension;
    }

    @Override
    public Collection<String> getDependencies() {
        return Arrays.asList(
                SubversionExtension.EXTENSION,
                JIRAExtension.EXTENSION
        );
    }

    @Override
    public List<? extends PropertyExtensionDescriptor> getPropertyExtensionDescriptors() {
        return Arrays.asList(
                sensibleFilesPropertyExtension,
                projectRootPathPropertyExtension
        );
    }

    @Override
    public Collection<? extends ActionExtension> getDiffActions() {
        return Collections.singletonList(changeLogActionController);
    }

    @Override
    public Collection<? extends EntityActionExtension> getEntityActions() {
        return Collections.singletonList(
                branchHistoryActionController
        );
    }
}
