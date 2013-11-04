package net.ontrack.extension.explanation;

import net.ontrack.core.model.Entity;
import net.ontrack.core.security.AuthorizationPolicy;
import net.ontrack.extension.api.property.AbstractPropertyExtensionDescriptor;
import net.sf.jstring.Strings;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Locale;

import static java.lang.String.format;

@Component
public class ExplanationPropertyExtension extends AbstractPropertyExtensionDescriptor {

    private final ExplanationConfiguration explanationConfiguration;

    @Autowired
    public ExplanationPropertyExtension(ExplanationConfiguration explanationConfiguration) {
        this.explanationConfiguration = explanationConfiguration;
    }

    @Override
    public EnumSet<Entity> getScope() {
        return EnumSet.of(Entity.VALIDATION_RUN);
    }

    @Override
    public String getExtension() {
        return ExplanationExtension.EXTENSION;
    }

    @Override
    public String getName() {
        return "explanation";
    }

    @Override
    public String getDisplayNameKey() {
        return "explanation";
    }

    @Override
    public AuthorizationPolicy getEditingAuthorizationPolicy(Entity entity) {
        return AuthorizationPolicy.LOGGED;
    }

    @Override
    public String getIconPath() {
        return "extension/explanation.png";
    }

    @Override
    public String editHTML(Strings strings, Locale locale, String value) {
        StringBuilder html = new StringBuilder();
        html.append(format(
                "<select id=\"extension-%1$s-%2$s\" name=\"extension-%1$s-%2$s\" class=\"input-xxlarge\">",
                getExtension(),
                getName()
        ));
        // Options
        html.append("<option value=\"\"></option>");
        for (String explanation : explanationConfiguration.getExplanations()) {
            html.append(format("<option value=\"%1$s\" %2$s>%1$s</option>",
                    StringEscapeUtils.escapeHtml4(explanation),
                    StringUtils.equals(explanation, value) ? "selected=\"selected\"" : ""));
        }
        // End
        html.append("</select>");
        // OK
        return html.toString();
    }
}
