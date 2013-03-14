package net.ontrack.extension.jenkins;

import net.ontrack.core.model.Entity;
import net.ontrack.extension.api.support.AbstractPropertyExtensionDescriptor;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
public class JenkinsUrlPropertyDescriptor extends AbstractPropertyExtensionDescriptor {

    @Override
    public EnumSet<Entity> getScope() {
        return EnumSet.allOf(Entity.class);
    }

    @Override
    public String getExtension() {
        return JenkinsExtension.EXTENSION;
    }

    @Override
    public String getName() {
        return "url";
    }

    /**
     * Renders a link, prefixed with an icon
     * @param value Value to render (link)
     * @return Image + Link
     */
    @Override
    public String toHTML(String value) {
        return String.format("<img src=\"jenkins\" /> <a href=\"%1$s\">%1$s</a>",
                value);
    }
}
