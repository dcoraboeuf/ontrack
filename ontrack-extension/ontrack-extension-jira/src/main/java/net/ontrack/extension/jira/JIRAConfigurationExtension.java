package net.ontrack.extension.jira;

import com.google.common.collect.Lists;
import net.ontrack.extension.api.configuration.ConfigurationExtension;
import net.ontrack.extension.api.configuration.ConfigurationExtensionField;
import net.ontrack.extension.api.configuration.PasswordConfigurationExtensionField;
import net.ontrack.extension.api.configuration.TextConfigurationExtensionField;

import java.util.List;

public class JIRAConfigurationExtension implements ConfigurationExtension {

    public static final String URL = "url";
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    private final JIRAConfiguration configuration = new JIRAConfiguration();

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
        // Converts to fields
        return Lists.newArrayList(
                new TextConfigurationExtensionField(URL, "jira.configuration.url", "http://jira", configuration.getUrl()),
                new TextConfigurationExtensionField(USER, "jira.configuration.user", "", configuration.getUser()),
                new PasswordConfigurationExtensionField(PASSWORD, "jira.configuration.password", configuration.getPassword())
        );
    }

    @Override
    public void configure(String name, String value) {
        switch (name) {
            case URL:
                configuration.setUrl(value);
                break;
            case USER:
                configuration.setUser(value);
                break;
            case PASSWORD:
                configuration.setPassword(value);
                break;
        }
    }
}
