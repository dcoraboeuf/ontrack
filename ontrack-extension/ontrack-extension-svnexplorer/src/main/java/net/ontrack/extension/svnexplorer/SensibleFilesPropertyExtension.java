package net.ontrack.extension.svnexplorer;

import net.ontrack.core.model.Entity;
import net.ontrack.core.security.AuthorizationPolicy;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.extension.api.property.AbstractPropertyExtensionDescriptor;
import net.sf.jstring.Strings;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Locale;

import static java.lang.String.format;

@Component
public class SensibleFilesPropertyExtension extends AbstractPropertyExtensionDescriptor {

    public static final String NAME = "sensibleFiles";

    @Override
    public EnumSet<Entity> getScope() {
        return EnumSet.of(Entity.BRANCH);
    }

    @Override
    public String getExtension() {
        return SVNExplorerExtension.EXTENSION;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDisplayNameKey() {
        return "svnexplorer.sensibleFiles";
    }

    @Override
    public String getIconPath() {
        return "extension/svnexplorer-sensiblefiles.png";
    }

    @Override
    public String toHTML(Strings strings, Locale locale, String value) {
        return value == null ? "" : StringEscapeUtils.escapeHtml4(value).replaceAll("\n", "<br/>");
    }

    @Override
    public String editHTML(Strings strings, Locale locale, String value) {
        return format(
                "<textarea id=\"extension-%1$s-%2$s\" name=\"extension-%1$s-%2$s\" maxlength=\"200\" rows=\"6\" class=\"input-xxlarge\">%3$s</textarea>",
                getExtension(), // 1
                getName(), // 2
                value != null ? StringEscapeUtils.escapeHtml4(value) : "" // 3
        );
    }

    @Override
    public String getRoleForEdition(Entity entity) {
        return SecurityRoles.ADMINISTRATOR;
    }

    @Override
    public AuthorizationPolicy getViewingAuthorizationPolicy(Entity entity) {
        return AuthorizationPolicy.PROJECT_CONFIG;
    }
}
