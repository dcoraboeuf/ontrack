package net.ontrack.extension.svn;

import net.ontrack.core.model.Entity;
import net.ontrack.core.security.AuthorizationPolicy;
import net.ontrack.extension.api.property.AbstractPropertyExtensionDescriptor;
import net.ontrack.extension.svn.service.SubversionService;
import net.sf.jstring.Strings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Locale;

@Component
@Deprecated
public class SubversionPathPropertyExtension extends AbstractPropertyExtensionDescriptor {

    public static final String PATH = "path";
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
        return PATH;
    }

    @Override
    public String getDisplayNameKey() {
        return "subversion.path";
    }

    @Override
    public String getIconPath() {
        return "extension/subversion.png";
    }

    @Override
    public String toHTML(Strings strings, Locale locale, Entity entity, int entityId, String path) {
        // FIXME Gets the repository from the project
        if (StringUtils.isBlank(path)) {
            return "";
        } else {
            return String.format(
                    "<a href=\"%s\">%s</a>",
                    subversionService.getBrowsingURL(null, path),
                    path
            );
        }
    }

    @Override
    public AuthorizationPolicy getEditingAuthorizationPolicy(Entity entity) {
        return AuthorizationPolicy.PROJECT_CONFIG;
    }
}
