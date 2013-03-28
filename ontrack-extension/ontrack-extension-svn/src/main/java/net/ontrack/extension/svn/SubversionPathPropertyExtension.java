package net.ontrack.extension.svn;

import net.ontrack.core.model.Entity;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.extension.api.property.AbstractPropertyExtensionDescriptor;
import net.ontrack.extension.svn.service.SubversionService;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Locale;

@Component
public class SubversionPathPropertyExtension extends AbstractPropertyExtensionDescriptor {

    private final SubversionService subversionService;

    @Autowired
    public SubversionPathPropertyExtension(SubversionService subversionService) {
        this.subversionService = subversionService;
    }

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
        return "path";
    }

    @Override
    public String getDisplayNameKey() {
        return "subversion.path";
    }

    @Override
    public String toHTML(Strings strings, Locale locale, String path) {
        // Logo
        StringBuilder html = new StringBuilder(
                String.format("<span title=\"%s\"><img src=\"extension/%s\" /> ",
                        strings.get(locale, getDisplayNameKey()),
                        "subversion.png"));
        // Path URL
        html.append(String.format("<a href=\"%s\">%s</a>",
                subversionService.getBrowsingURL(path),
                path
                ));
        // End
        html.append("</span>");
        return html.toString();
    }

    @Override
    public String getRoleForEdition(Entity entity) {
        return SecurityRoles.ADMINISTRATOR;
    }
}
