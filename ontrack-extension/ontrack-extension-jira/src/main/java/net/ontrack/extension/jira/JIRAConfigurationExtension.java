package net.ontrack.extension.jira;

import net.ontrack.extension.api.configuration.ConfigurationExtension;

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
}
