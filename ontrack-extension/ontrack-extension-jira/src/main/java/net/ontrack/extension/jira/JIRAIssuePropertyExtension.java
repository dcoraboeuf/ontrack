package net.ontrack.extension.jira;

import net.ontrack.core.model.Entity;
import net.ontrack.core.model.Status;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.core.support.InputException;
import net.ontrack.extension.api.property.AbstractPropertyExtensionDescriptor;
import net.sf.jstring.Strings;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Locale;
import java.util.regex.Pattern;

import static java.lang.String.format;

@Component
public class JIRAIssuePropertyExtension extends AbstractPropertyExtensionDescriptor {

    private final Pattern ISSUE_PATTERN = Pattern.compile("[A-Za-z][A-Za-z0-9]*\\-[0-9]+");

    private final JIRAConfigurationExtension jiraConfigurationExtension;

    @Autowired
    public JIRAIssuePropertyExtension(JIRAConfigurationExtension jiraConfigurationExtension) {
        this.jiraConfigurationExtension = jiraConfigurationExtension;
    }

    @Override
    public EnumSet<Entity> getScope() {
        return EnumSet.of(Entity.VALIDATION_RUN);
    }

    /**
     * A user can enter an issue for the DEFECTIVE status
     */
    @Override
    public EnumSet<Status> getStatuses() {
        return EnumSet.of(Status.DEFECTIVE);
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

    @Override
    public void validate(String value) throws InputException {
        if (!ISSUE_PATTERN.matcher(value).matches()) {
            throw new JIRAIssuePatternException(value);
        }
    }

    @Override
    public String toHTML(Strings strings, Locale locale, String value) {
        String issueUrl = jiraConfigurationExtension.getIssueURL(value);
        return String.format("<span title=\"%s\"><img src=\"extension/%s\" /> <a href=\"%s\">%s</a></span>",
                strings.get(locale, getDisplayNameKey()),
                "jira.png",
                issueUrl,
                value
        );
    }

    @Override
    public String editHTML(Strings strings, Locale locale, String value) {
        return format(
                "<input id=\"extension-%1$s-%2$s\" name=\"extension-%1$s-%2$s\" type=\"text\" maxlength=\"200\" class=\"input-small\" value=\"%3$s\" />",
                getExtension(), // 1
                getName(), // 2
                value != null ? StringEscapeUtils.escapeHtml4(value) : "" // 3
        );
    }

    @Override
    public String getRoleForEdition(Entity entity) {
        return SecurityRoles.USER;
    }
}
