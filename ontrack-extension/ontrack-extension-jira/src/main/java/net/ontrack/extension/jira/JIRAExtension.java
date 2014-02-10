package net.ontrack.extension.jira;

import net.ontrack.extension.api.action.TopActionExtension;
import net.ontrack.extension.api.configuration.ConfigurationExtension;
import net.ontrack.extension.api.property.PropertyExtensionDescriptor;
import net.ontrack.extension.api.support.ExtensionAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class JIRAExtension extends ExtensionAdapter {

    public static final String EXTENSION = "jira";
    private final JIRAIssuePropertyExtension issuePropertyExtension;
    private final JIRAConfigurationPropertyExtension jiraConfigurationPropertyExtension;
    private final JIRAConfigurationExtension configurationExtension;
    private final JIRAConfigurationGUIController jiraConfigurationGUIController;

    @Autowired
    public JIRAExtension(JIRAIssuePropertyExtension issuePropertyExtension, JIRAConfigurationPropertyExtension jiraConfigurationPropertyExtension, JIRAConfigurationExtension configurationExtension, JIRAConfigurationGUIController jiraConfigurationGUIController) {
        super(EXTENSION);
        this.issuePropertyExtension = issuePropertyExtension;
        this.jiraConfigurationPropertyExtension = jiraConfigurationPropertyExtension;
        this.configurationExtension = configurationExtension;
        this.jiraConfigurationGUIController = jiraConfigurationGUIController;
    }

    @Override
    public List<? extends ConfigurationExtension> getConfigurationExtensions() {
        return Collections.singletonList(configurationExtension);
    }

    @Override
    public List<? extends PropertyExtensionDescriptor> getPropertyExtensionDescriptors() {
        return Arrays.asList(issuePropertyExtension, jiraConfigurationPropertyExtension);
    }

    @Override
    public Collection<? extends TopActionExtension> getTopLevelActions() {
        return Collections.singletonList(jiraConfigurationGUIController);
    }
}
