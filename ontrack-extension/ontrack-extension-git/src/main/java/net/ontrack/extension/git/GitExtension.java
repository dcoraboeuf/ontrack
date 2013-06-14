package net.ontrack.extension.git;

import net.ontrack.extension.api.support.ExtensionAdapter;
import org.springframework.stereotype.Component;

@Component
public class GitExtension extends ExtensionAdapter {

    public static final String EXTENSION = "git";

    public GitExtension() {
        super(EXTENSION);
    }
}
