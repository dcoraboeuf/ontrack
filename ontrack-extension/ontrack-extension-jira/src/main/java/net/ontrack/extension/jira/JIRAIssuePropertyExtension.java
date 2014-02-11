package net.ontrack.extension.jira;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.ValidationRunSummary;
import net.ontrack.core.security.AuthorizationPolicy;
import net.ontrack.core.support.InputException;
import net.ontrack.extension.api.property.AbstractPropertyExtensionDescriptor;
import net.ontrack.extension.jira.service.model.JIRAConfiguration;
import net.ontrack.service.ManagementService;
import net.sf.jstring.Strings;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Locale;

@Component
public class JIRAIssuePropertyExtension extends AbstractPropertyExtensionDescriptor {

    private static final String ISSUE_SEPARATORS = ",; ";
    private final ManagementService managementService;
    private final JIRAService jiraService;

    @Autowired
    public JIRAIssuePropertyExtension(ManagementService managementService, JIRAService jiraService) {
        this.managementService = managementService;
        this.jiraService = jiraService;
    }

    @Override
    public EnumSet<Entity> getScope() {
        return EnumSet.of(Entity.VALIDATION_RUN);
    }

    @Override
    public String getExtension() {
        return "jira";
    }

    @Override
    public String getName() {
        return "issue";
    }

    @Override
    public String getDisplayNameKey() {
        return "jira.issue";
    }

    protected String[] parseIssues(String value) {
        return StringUtils.split(value, ISSUE_SEPARATORS);
    }

    @Override
    public void validate(String value) throws InputException {
        String[] issues = parseIssues(value);
        for (String issue : issues) {
            if (!jiraService.isIssue(issue)) {
                throw new JIRAIssuePatternException(issue);
            }
        }
    }

    @Override
    public String getIconPath() {
        return "extension/jira.png";
    }

    @Override
    public String toHTML(final Strings strings, final Locale locale, Entity entity, int entityId, String value) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        StringBuilder html = new StringBuilder();
        // Gets the validation run
        Validate.isTrue(entity == Entity.VALIDATION_RUN, "Expecting validation run");
        ValidationRunSummary validationRun = managementService.getValidationRun(entityId);
        // Gets the project
        int projectId = validationRun.getValidationStamp().getBranch().getProject().getId();
        // Gets the project JIRA configuration
        final JIRAConfiguration jiraConfiguration = jiraService.getConfigurationForProject(projectId);
        // For each issue
        html.append(
                StringUtils.join(
                        Iterables.transform(
                                Arrays.asList(parseIssues(value)),
                                new Function<String, String>() {
                                    @Override
                                    public String apply(String issue) {
                                        return String.format("<a href=\"%s\">%s</a>",
                                                jiraConfiguration.getIssueURL(issue),
                                                StringEscapeUtils.escapeHtml4(issue)
                                        );
                                    }
                                }
                        ),
                        ", "
                )
        );
        // End
        return html.toString();
    }

    @Override
    public AuthorizationPolicy getEditingAuthorizationPolicy(Entity entity) {
        return AuthorizationPolicy.LOGGED;
    }
}
