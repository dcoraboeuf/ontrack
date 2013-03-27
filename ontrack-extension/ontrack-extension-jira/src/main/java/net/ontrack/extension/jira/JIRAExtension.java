package net.ontrack.extension.jira;

import net.ontrack.extension.api.support.ExtensionAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.regex.Pattern;

@Component
public class JIRAExtension extends ExtensionAdapter {

    public static final String EXTENSION = "jira";
    public static final Pattern ISSUE_PATTERN = Pattern.compile("[A-Za-z][A-Za-z0-9]*\\-[0-9]+");

    @Autowired
    public JIRAExtension(JIRAIssuePropertyExtension propertyExtension, JIRAConfigurationExtension configurationExtension) {
        super(
                EXTENSION,
                Collections.singletonList(propertyExtension),
                Collections.singletonList(configurationExtension));
    }
}
