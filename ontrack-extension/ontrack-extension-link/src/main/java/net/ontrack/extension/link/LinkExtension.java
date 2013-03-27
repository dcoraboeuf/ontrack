package net.ontrack.extension.link;

import net.ontrack.extension.api.support.ExtensionAdapter;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class LinkExtension extends ExtensionAdapter {

    public static final String EXTENSION = "link";

    public LinkExtension() {
        super(
                EXTENSION,
                Collections.singletonList(new LinkPropertyDescriptor()));
    }
}
