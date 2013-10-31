package net.ontrack.extension.github;

import net.ontrack.core.model.Entity;
import net.ontrack.core.security.AuthorizationPolicy;
import net.ontrack.extension.api.property.AbstractPropertyExtensionDescriptor;
import net.sf.jstring.Strings;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Locale;

@Component
public class GitHubProjectProperty extends AbstractPropertyExtensionDescriptor {

    public static final String NAME = "project";

    @Override
    public EnumSet<Entity> getScope() {
        return EnumSet.of(Entity.PROJECT);
    }

    @Override
    public String getExtension() {
        return GitHubExtension.EXTENSION;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDisplayNameKey() {
        return "github.project";
    }

    @Override
    public String getIconPath() {
        return "extension/github.jpg";
    }

    @Override
    public String toHTML(Strings strings, Locale locale, String value) {
        return String.format(
                "<a href=\"https://github.com/%1$s\">%1$s</a>",
                value);
    }

    @Override
    public AuthorizationPolicy getEditingAuthorizationPolicy(Entity entity) {
        return AuthorizationPolicy.PROJECT_CONFIG;
    }
}
