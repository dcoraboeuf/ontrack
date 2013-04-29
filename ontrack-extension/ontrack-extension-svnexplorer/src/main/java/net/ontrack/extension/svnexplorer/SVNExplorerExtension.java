package net.ontrack.extension.svnexplorer;

import net.ontrack.extension.api.action.ActionExtension;
import net.ontrack.extension.api.action.EntityActionExtension;
import net.ontrack.extension.api.property.PropertyExtensionDescriptor;
import net.ontrack.extension.api.support.ExtensionAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class SVNExplorerExtension extends ExtensionAdapter {

    public static final String EXTENSION = "svnexplorer";
    private final ChangeLogActionController changeLogActionController;
    private final SensibleFilesPropertyExtension sensibleFilesPropertyExtension;
    private final BranchHistoryActionController branchHistoryActionController;

    @Autowired
    public SVNExplorerExtension(
            ChangeLogActionController changeLogActionController,
            SensibleFilesPropertyExtension sensibleFilesPropertyExtension, BranchHistoryActionController branchHistoryActionController) {
        super(EXTENSION);
        this.changeLogActionController = changeLogActionController;
        this.sensibleFilesPropertyExtension = sensibleFilesPropertyExtension;
        this.branchHistoryActionController = branchHistoryActionController;
    }

    @Override
    public List<? extends PropertyExtensionDescriptor> getPropertyExtensionDescriptors() {
        return Collections.singletonList(sensibleFilesPropertyExtension);
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
