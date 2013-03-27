package net.ontrack.extension.svnexplorer;

import net.ontrack.extension.api.action.ActionExtension;
import net.ontrack.extension.api.configuration.ConfigurationExtension;
import net.ontrack.extension.api.property.PropertyExtensionDescriptor;
import net.ontrack.extension.api.support.ExtensionAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class SVNExplorerExtension extends ExtensionAdapter {

    public static final String EXTENSION = "svnexplorer";

    @Autowired
    public SVNExplorerExtension(ChangeLogActionController changeLogActionController) {
        super(
                EXTENSION,
                Collections.<PropertyExtensionDescriptor>emptyList(),
                Collections.<ConfigurationExtension>emptyList(),
                Collections.<ActionExtension>emptyList(),
                Collections.singletonList(changeLogActionController));
    }

}
