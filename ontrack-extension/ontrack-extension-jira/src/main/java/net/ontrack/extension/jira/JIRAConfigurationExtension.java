package net.ontrack.extension.jira;

import com.google.common.collect.Lists;
import net.ontrack.extension.api.configuration.ConfigurationExtension;
import net.ontrack.extension.api.configuration.ConfigurationExtensionField;
import net.ontrack.extension.api.configuration.PasswordConfigurationExtensionField;
import net.ontrack.extension.api.configuration.TextConfigurationExtensionField;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JIRAConfigurationExtension implements ConfigurationExtension {

    public static final String URL = "url";
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    private String url;
    private String user;
    private String password;

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

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
                new TextConfigurationExtensionField(URL, "jira.configuration.url", "http://jira", getUrl()),
                new TextConfigurationExtensionField(USER, "jira.configuration.user", "", getUser()),
                new PasswordConfigurationExtensionField(PASSWORD, "jira.configuration.password", getPassword())
        );
    }

    @Override
    public void configure(String name, String value) {
        switch (name) {
            case URL:
                url = value;
                break;
            case USER:
                user = value;
                break;
            case PASSWORD:
                password = value;
                break;
        }
    }

    public String getIssueURL(String issue) {
        String base = getUrl();
        if (StringUtils.isNotBlank(base)) {
            return String.format("%s/browse/%s", base, issue);
        } else {
            return issue;
        }
    }
}
