package net.ontrack.extension.jira;

import net.ontrack.core.model.Entity;
import net.ontrack.core.security.AuthorizationPolicy;
import net.ontrack.core.security.ProjectFunction;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.extension.api.property.AbstractPropertyExtensionDescriptor;
import net.ontrack.extension.jira.service.model.JIRAConfiguration;
import net.sf.jstring.Strings;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import static java.lang.String.format;

@Component
public class JIRAConfigurationPropertyExtension extends AbstractPropertyExtensionDescriptor {

    public static final String NAME = "configuration";
    private final JIRAConfigurationService jiraConfigurationService;
    private final SecurityUtils securityUtils;

    @Autowired
    public JIRAConfigurationPropertyExtension(JIRAConfigurationService jiraConfigurationService, SecurityUtils securityUtils) {
        this.jiraConfigurationService = jiraConfigurationService;
        this.securityUtils = securityUtils;
    }

    /**
     * Attached to projects only.
     */
    @Override
    public EnumSet<Entity> getScope() {
        return EnumSet.of(Entity.PROJECT);
    }

    @Override
    public String getExtension() {
        return "jira";
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDisplayNameKey() {
        return "jira.configuration.property";
    }

    @Override
    public String getIconPath() {
        return "extension/jira.png";
    }

    @Override
    public AuthorizationPolicy getEditingAuthorizationPolicy(Entity entity) {
        return AuthorizationPolicy.forProject(ProjectFunction.PROJECT_CONFIG);
    }

    /**
     * The value of the property is the ID of the corresponding JIRA configuration. The
     * display field must display:
     * <ol>
     * <li>The name of the configuration</li>
     * <li>The URL of the JIRA server as a clickable link</li>
     * </ol>
     */
    @Override
    public String toHTML(Strings strings, Locale locale, Entity entity, int entityId, String value) {
        if (StringUtils.isNotBlank(value)) {
            final int jiraConfigurationId = Integer.parseInt(value, 10);
            JIRAConfiguration jiraConfiguration = securityUtils.asAdmin(
                    new Callable<JIRAConfiguration>() {
                        @Override
                        public JIRAConfiguration call() throws Exception {
                            return jiraConfigurationService.getConfigurationById(jiraConfigurationId);
                        }
                    }
            );
            return String.format("<a href=\"%s\">%s</a>",
                    StringEscapeUtils.escapeHtml4(jiraConfiguration.getUrl()),
                    StringEscapeUtils.escapeHtml4(jiraConfiguration.getName())
            );
        } else {
            return "";
        }
    }

    /**
     * Displays a combo box with all the available JIRA configuration, with the project's one
     * being the one selected.
     */
    @Override
    public String editHTML(Strings strings, Locale locale, String value) {
        StringBuilder html = new StringBuilder();
        html.append(format(
                "<select id=\"extension-%1$s-%2$s\" name=\"extension-%1$s-%2$s\" class=\"input-large\">",
                getExtension(),
                getName()
        ));
        // Options
        html.append("<option value=\"\"></option>");
        List<JIRAConfiguration> allConfigurations = securityUtils.asAdmin(new Callable<List<JIRAConfiguration>>() {
            public List<JIRAConfiguration> call() throws Exception {
                return jiraConfigurationService.getAllConfigurations();
            }
        });
        for (JIRAConfiguration jiraConfiguration : allConfigurations) {
            html.append(format("<option value=\"%1$s\" %3$s>%2$s</option>",
                    jiraConfiguration.getId(),
                    StringEscapeUtils.escapeHtml4(jiraConfiguration.getName()),
                    StringUtils.equals(String.valueOf(jiraConfiguration.getId()), value) ? "selected=\"selected\"" : ""));
        }
        // End
        html.append("</select>");
        // OK
        return html.toString();
    }
}
