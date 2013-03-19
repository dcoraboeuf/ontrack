package net.ontrack.extension.jira;

import net.ontrack.core.model.Entity;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.core.support.InputException;
import net.ontrack.extension.api.support.AbstractPropertyExtensionDescriptor;
import net.sf.jstring.Strings;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Locale;
import java.util.regex.Pattern;

import static java.lang.String.format;

@Component
public class JIRAIssuePropertyExtension extends AbstractPropertyExtensionDescriptor {

    private final Pattern ISSUE_PATTERN = Pattern.compile("[A-Za-z][A-Za-z0-9]*\\-[0-9]+");

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

    @Override
    public void validate(String value) throws InputException {
        if (!ISSUE_PATTERN.matcher(value).matches()) {
            throw new JIRAIssuePatternException(value);
        }
    }

    @Override
    public String toHTML(Strings strings, Locale locale, String value) {
        return String.format("<span title=\"%s\"><img src=\"extension/%s\" /> <a href=\"%s\">%s</a></span>",
                strings.get(locale, getDisplayNameKey()),
                "jira.png",
                value, // FIXME Href to the issue
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
