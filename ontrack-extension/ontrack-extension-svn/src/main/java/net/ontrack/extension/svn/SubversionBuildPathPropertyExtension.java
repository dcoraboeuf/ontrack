package net.ontrack.extension.svn;

import net.ontrack.core.model.Entity;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.extension.api.property.AbstractPropertyExtensionDescriptor;
import net.sf.jstring.Strings;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Locale;

@Component
public class SubversionBuildPathPropertyExtension extends AbstractPropertyExtensionDescriptor {

    @Override
    public EnumSet<Entity> getScope() {
        return EnumSet.of(Entity.BRANCH);
    }

    @Override
    public String getExtension() {
        return SubversionExtension.EXTENSION;
    }

    @Override
    public String getName() {
        return SubversionExtension.SUBVERSION_BUILD_PATH;
    }

    @Override
    public String getDisplayNameKey() {
        return "subversion.buildPath";
    }

    @Override
    public String toHTML(Strings strings, Locale locale, String path) {
        // Logo
        StringBuilder html = new StringBuilder(
                String.format("<span title=\"%s\"><img src=\"extension/%s\" /> ",
                        strings.get(locale, getDisplayNameKey()),
                        "subversion.png"));
        // Path
        html.append(StringEscapeUtils.escapeHtml4(path));
        // End
        html.append("</span>");
        return html.toString();
    }

    @Override
    public String getRoleForEdition(Entity entity) {
        return SecurityRoles.ADMINISTRATOR;
    }

    /**
     * Visible only by administrators
     */
    @Override
    public String getRoleForView(Entity entity) {
        return SecurityRoles.ADMINISTRATOR;
    }
}
