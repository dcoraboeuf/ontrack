package net.ontrack.extension.svn;

import com.google.common.collect.Lists;
import net.ontrack.extension.api.configuration.ConfigurationExtension;
import net.ontrack.extension.api.configuration.ConfigurationExtensionField;
import net.ontrack.extension.api.configuration.PasswordConfigurationExtensionField;
import net.ontrack.extension.api.configuration.TextConfigurationExtensionField;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SubversionConfigurationExtension implements ConfigurationExtension {

    public static final String URL = "url";
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static final String BRANCH_PATTERN = "branchPattern";
    public static final String TAG_PATTERN = "tagPattern";
    public static final String TAG_FILTER_PATTERN = "tagFilterPattern";
    private final SubversionConfiguration configuration = new SubversionConfiguration();

    public SubversionConfiguration getConfiguration() {
        return configuration;
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
                new TextConfigurationExtensionField(URL, "subversion.configuration.url", "http://subversion", configuration.getUrl()),
                new TextConfigurationExtensionField(USER, "subversion.configuration.user", "", configuration.getUser()),
                new PasswordConfigurationExtensionField(PASSWORD, "subversion.configuration.password", configuration.getPassword()),
                new TextConfigurationExtensionField(BRANCH_PATTERN, "subversion.configuration.branchPattern", ".+/branches/[_\\.\\-0-9a-zA-Z]+", configuration.getBranchPattern()),
                new TextConfigurationExtensionField(TAG_PATTERN, "subversion.configuration.tagPattern", ".+/(tags|int)/.+", configuration.getTagPattern()),
                new TextConfigurationExtensionField(TAG_FILTER_PATTERN, "subversion.configuration.tagFilterPattern", "", configuration.getTagFilterPattern())
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
            case BRANCH_PATTERN:
                configuration.setBranchPattern(value);
                break;
            case TAG_PATTERN:
                configuration.setTagPattern(value);
                break;
            case TAG_FILTER_PATTERN:
                configuration.setTagFilterPattern(value);
                break;
        }
    }
}
