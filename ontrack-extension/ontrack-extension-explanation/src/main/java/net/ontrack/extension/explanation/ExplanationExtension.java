package net.ontrack.extension.explanation;

import net.ontrack.extension.api.property.PropertyExtensionDescriptor;
import net.ontrack.extension.api.support.ExtensionAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

@Component
public class ExplanationExtension extends ExtensionAdapter {

    public static final String EXTENSION = "explanation";

    @Autowired
    public ExplanationExtension(ExplanationConfiguration configuration) {
        super(
                EXTENSION,
                Collections.<PropertyExtensionDescriptor>emptyList(),
                Arrays.asList(
                        configuration
                ));
    }
}
