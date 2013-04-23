package net.ontrack.extension.link;

import net.ontrack.extension.api.property.PropertyExtensionDescriptor;
import net.ontrack.extension.api.support.ExtensionAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class LinkExtension extends ExtensionAdapter {

    public static final String EXTENSION = "link";
    private final LinkPropertyDescriptor linkPropertyDescriptor;

    @Autowired
    public LinkExtension(LinkPropertyDescriptor linkPropertyDescriptor) {
        super(EXTENSION);
        this.linkPropertyDescriptor = linkPropertyDescriptor;
    }

    @Override
    public List<? extends PropertyExtensionDescriptor> getPropertyExtensionDescriptors() {
        return Collections.singletonList(linkPropertyDescriptor);
    }
}
