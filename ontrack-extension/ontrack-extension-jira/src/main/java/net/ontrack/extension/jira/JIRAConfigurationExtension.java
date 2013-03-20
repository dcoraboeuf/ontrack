package net.ontrack.extension.jira;

import com.google.common.collect.Lists;
import net.ontrack.extension.api.configuration.ConfigurationExtension;
import net.ontrack.extension.api.configuration.ConfigurationExtensionField;
import net.ontrack.extension.api.configuration.PasswordConfigurationExtensionField;
import net.ontrack.extension.api.configuration.TextConfigurationExtensionField;

import java.util.List;

public class JIRAConfigurationExtension implements ConfigurationExtension {
    @Override
    public String getExtension() {
        return JIRAExtension.EXTENSION;
    }

    @Override
    public String getName() {
        return "configuration";
    }

    @Override
    public String getTitleKey() {
        return "jira.configuration";
    }

    @Override
    public List<? extends ConfigurationExtensionField> getFields() {
        // FIXME Gets the JIRA configuration
        JIRAConfiguration c = new JIRAConfiguration();
        // Converts to fields
        return Lists.newArrayList(
                new TextConfigurationExtensionField("url", "jira.configuration.url", "http://jira", c.getUrl()),
                new TextConfigurationExtensionField("user", "jira.configuration.user", "", c.getUser()),
                new PasswordConfigurationExtensionField("password", "jira.configuration.password", c.getPassword())
        );
    }
}
