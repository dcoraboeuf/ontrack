package net.ontrack.extension.link;

import net.ontrack.extension.api.property.PropertyExtensionDescriptor;
import net.ontrack.extension.api.support.ExtensionAdapter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class LinkExtension extends ExtensionAdapter {

    public static final String EXTENSION = "link";

    public LinkExtension() {
        super(EXTENSION);
    }

    @Override
    public List<? extends PropertyExtensionDescriptor> getPropertyExtensionDescriptors() {
        return Collections.singletonList(new LinkPropertyDescriptor());
    }
}
