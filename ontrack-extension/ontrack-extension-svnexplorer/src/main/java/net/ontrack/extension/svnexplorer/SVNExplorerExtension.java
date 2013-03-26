package net.ontrack.extension.svnexplorer;

import net.ontrack.extension.api.property.PropertyExtensionDescriptor;
import net.ontrack.extension.api.support.ExtensionAdapter;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class SVNExplorerExtension extends ExtensionAdapter {

    public static final String EXTENSION = "svnexplorer";

    public SVNExplorerExtension() {
        super(EXTENSION, Collections.<PropertyExtensionDescriptor>emptyList());
    }

}
