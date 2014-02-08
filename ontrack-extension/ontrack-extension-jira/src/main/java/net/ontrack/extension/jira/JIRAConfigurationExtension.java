package net.ontrack.extension.jira;

import com.google.common.collect.Lists;
import net.ontrack.extension.api.configuration.ConfigurationExtension;
import net.ontrack.extension.api.configuration.ConfigurationExtensionField;
import net.ontrack.extension.api.configuration.PasswordConfigurationExtensionField;
import net.ontrack.extension.api.configuration.TextConfigurationExtensionField;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Component
@Deprecated
public class JIRAConfigurationExtension implements ConfigurationExtension {

    public static final String URL = "url";
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static final String EXCLUSIONS = "exclusions";
    public static final Pattern ISSUE_PATTERN = Pattern.compile("[A-Za-z][A-Za-z0-9]*\\-[0-9]+");
    private String url;
    private String user;
    private String password;
    private String exclusions;
    private Set<String> excludedProjects = Collections.emptySet();
    private Set<String> excludedIssues = Collections.emptySet();

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getExclusions() {
        return exclusions;
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
                new PasswordConfigurationExtensionField(PASSWORD, "jira.configuration.password", getPassword()),
                new TextConfigurationExtensionField(EXCLUSIONS, "jira.configuration.exclusions", "", getExclusions())
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
            case EXCLUSIONS:
                exclusions = value;
                setupExclusions();
                break;
        }
    }

    private void setupExclusions() {
        excludedProjects = new HashSet<>();
        excludedIssues = new HashSet<>();
        String[] tokens = StringUtils.split(exclusions, ",");
        if (tokens != null) {
            for (String token : tokens) {
                int index = token.indexOf("-");
                if (index > 0) {
                    excludedIssues.add(token);
                } else {
                    excludedProjects.add(token);
                }
            }
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

    public boolean isIssue(String token) {
        return ISSUE_PATTERN.matcher(token).matches()
                && !isIssueExcluded(token);
    }

    private boolean isIssueExcluded(String token) {
        return excludedIssues.contains(token)
                || excludedProjects.contains(StringUtils.substringBefore(token, "-"));
    }
}
