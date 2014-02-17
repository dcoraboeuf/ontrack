package net.ontrack.extension.svn;

import net.ontrack.core.model.Entity;
import net.ontrack.core.security.AuthorizationPolicy;
import net.ontrack.extension.api.property.AbstractPropertyExtensionDescriptor;
import net.ontrack.extension.svn.service.RepositoryService;
import net.ontrack.extension.svn.service.model.SVNRepository;
import net.sf.jstring.Strings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Locale;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

@Component
public class SubversionRepositoryPropertyExtension extends AbstractPropertyExtensionDescriptor {

    public static final String NAME = "repository";

    private final RepositoryService repositoryService;

    @Autowired
    public SubversionRepositoryPropertyExtension(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @Override
    public EnumSet<Entity> getScope() {
        return EnumSet.of(Entity.PROJECT);
    }

    @Override
    public String getExtension() {
        return SubversionExtension.EXTENSION;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDisplayNameKey() {
        return "subversion.repository";
    }

    @Override
    public String getIconPath() {
        return "extension/subversion.png";
    }

    @Override
    public String toHTML(Strings strings, Locale locale, Entity entity, int entityId, String repositoryId) {
        if (StringUtils.isNotBlank(repositoryId)) {
            SVNRepository repository = repositoryService.getRepositorySummary(Integer.parseInt(repositoryId, 10));
            return String.format(
                    "%s (%s)",
                    escapeHtml4(repository.getName()),
                    escapeHtml4(repository.getUrl())
            );
        } else {
            return "";
        }
    }

    @Override
    public String editHTML(Strings strings, Locale locale, String repositoryId) {
        StringBuilder html = new StringBuilder();
        html.append(format(
                "<select id=\"extension-%1$s-%2$s\" name=\"extension-%1$s-%2$s\" class=\"input-xxlarge\">",
                getExtension(),
                getName()
        ));
        // Options
        html.append("<option value=\"\">&nbsp;</option>");
        for (SVNRepository repository : repositoryService.getAllRepositories()) {
            html.append(format("<option value=\"%s\" %s>%s</option>",
                    String.valueOf(repository.getId()),
                    (StringUtils.isNotBlank(repositoryId) && (repository.getId() == Integer.parseInt(repositoryId, 10))) ? "selected=\"selected\"" : "",
                    escapeHtml4(repository.getName())
            ));
        }
        // End
        html.append("</select>");
        // OK
        return html.toString();
    }

    @Override
    public AuthorizationPolicy getEditingAuthorizationPolicy(Entity entity) {
        return AuthorizationPolicy.PROJECT_CONFIG;
    }
}
