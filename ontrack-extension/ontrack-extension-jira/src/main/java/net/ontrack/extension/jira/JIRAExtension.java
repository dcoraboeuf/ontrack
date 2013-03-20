package net.ontrack.extension.jira;

import net.ontrack.extension.api.support.ExtensionAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class JIRAExtension extends ExtensionAdapter {

    public static final String EXTENSION = "jira";

    @Autowired
    public JIRAExtension(JIRAIssuePropertyExtension propertyExtension, JIRAConfigurationExtension configurationExtension) {
        super(
                EXTENSION,
                Collections.singletonList(propertyExtension),
                Collections.singletonList(configurationExtension));
    }
}
