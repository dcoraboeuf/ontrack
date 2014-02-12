package net.ontrack.extension.svn;

import com.google.common.collect.Lists;
import net.ontrack.extension.api.configuration.ConfigurationExtension;
import net.ontrack.extension.api.configuration.ConfigurationExtensionField;
import net.ontrack.extension.api.configuration.PasswordConfigurationExtensionField;
import net.ontrack.extension.api.configuration.TextConfigurationExtensionField;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Deprecated
public class SubversionConfigurationExtension implements ConfigurationExtension {

    public static final String URL = "url";
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static final String BRANCH_PATTERN = "branchPattern";
    public static final String TAG_PATTERN = "tagPattern";
    public static final String TAG_FILTER_PATTERN = "tagFilterPattern";
    public static final String BROWSER_FOR_PATH = "browserForPath";
    public static final String BROWSER_FOR_REVISION = "browserForRevision";
    public static final String BROWSER_FOR_CHANGE = "browserForChange";

    private String url;
    private String user;
    private String password;
    private String branchPattern;
    private String tagPattern;
    private String tagFilterPattern;
    private String browserForPath;
    private String browserForRevision;
    private String browserForChange;

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getBranchPattern() {
        return branchPattern;
    }

    public String getTagPattern() {
        return tagPattern;
    }

    public String getTagFilterPattern() {
        return tagFilterPattern;
    }

    public String getBrowserForPath() {
        return browserForPath;
    }

    public String getBrowserForRevision() {
        return browserForRevision;
    }

    public String getBrowserForChange() {
        return browserForChange;
    }

    @Override
    public String getExtension() {
        return SubversionExtension.EXTENSION;
    }

    @Override
    public String getName() {
        return "subversion";
    }

    @Override
    public String getTitleKey() {
        return "subversion.configuration";
    }

    @Override
    public List<? extends ConfigurationExtensionField> getFields() {
        // Converts to fields
        return Lists.newArrayList(
                new TextConfigurationExtensionField(URL, "subversion.configuration.url", "http://subversion", url),
                new TextConfigurationExtensionField(USER, "subversion.configuration.user", "", user),
                new PasswordConfigurationExtensionField(PASSWORD, "subversion.configuration.password", password),
                new TextConfigurationExtensionField(BRANCH_PATTERN, "subversion.configuration.branchPattern", ".+/branches/[_\\.\\-0-9a-zA-Z]+", branchPattern),
                new TextConfigurationExtensionField(TAG_PATTERN, "subversion.configuration.tagPattern", ".+/(tags|int)/.+", tagPattern),
                new TextConfigurationExtensionField(TAG_FILTER_PATTERN, "subversion.configuration.tagFilterPattern", "", tagFilterPattern),
                new TextConfigurationExtensionField(BROWSER_FOR_PATH, "subversion.configuration.browserForPath", "", browserForPath),
                new TextConfigurationExtensionField(BROWSER_FOR_REVISION, "subversion.configuration.browserForRevision", "", browserForRevision),
                new TextConfigurationExtensionField(BROWSER_FOR_CHANGE, "subversion.configuration.browserForChange", "", browserForChange)
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
            case BRANCH_PATTERN:
                branchPattern = value;
                break;
            case TAG_PATTERN:
                tagPattern = value;
                break;
            case TAG_FILTER_PATTERN:
                tagFilterPattern = value;
                break;
            case BROWSER_FOR_PATH:
                browserForPath = value;
                break;
            case BROWSER_FOR_REVISION:
                browserForRevision = value;
                break;
            case BROWSER_FOR_CHANGE:
                browserForChange = value;
                break;
        }
    }
}
