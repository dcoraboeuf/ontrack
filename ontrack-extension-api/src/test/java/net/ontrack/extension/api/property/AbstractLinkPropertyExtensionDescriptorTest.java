package net.ontrack.extension.api.property;

import net.ontrack.core.model.Entity;
import org.junit.Test;

import java.util.EnumSet;

public class AbstractLinkPropertyExtensionDescriptorTest {

    private final AbstractLinkPropertyExtensionDescriptor descriptor = new AbstractLinkPropertyExtensionDescriptor("extension.link.text", "extension/link-test") {
        @Override
        public EnumSet<Entity> getScope() {
            return EnumSet.of(Entity.PROJECT);
        }

        @Override
        public String getExtension() {
            return "test";
        }

        @Override
        public String getName() {
            return "link";
        }
    };

    @Test(expected = InvalidURLException.class)
    public void validate_spaces() {
        String link = " http://test ";
        descriptor.validate(link);
    }

    @Test(expected = InvalidURLException.class)
    public void validate_protocol() {
        String link = " xxx://test ";
        descriptor.validate(link);
    }

    @Test
    public void validate_ok() {
        String link = "http://test";
        descriptor.validate(link);
    }

}
