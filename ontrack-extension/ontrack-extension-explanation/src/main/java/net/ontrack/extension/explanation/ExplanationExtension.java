package net.ontrack.extension.explanation;

import net.ontrack.extension.api.configuration.ConfigurationExtension;
import net.ontrack.extension.api.property.PropertyExtensionDescriptor;
import net.ontrack.extension.api.support.ExtensionAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ExplanationExtension extends ExtensionAdapter {

    public static final String EXTENSION = "explanation";
    private final ExplanationPropertyExtension explanationPropertyExtension;
    private final ExplanationConfiguration explanationConfiguration;

    @Autowired
    public ExplanationExtension(
            ExplanationPropertyExtension explanationPropertyExtension,
            ExplanationConfiguration explanationConfiguration) {
        super(EXTENSION);
        this.explanationPropertyExtension = explanationPropertyExtension;
        this.explanationConfiguration = explanationConfiguration;
    }

    @Override
    public List<? extends PropertyExtensionDescriptor> getPropertyExtensionDescriptors() {
        return Collections.singletonList(explanationPropertyExtension);
    }

    @Override
    public List<? extends ConfigurationExtension> getConfigurationExtensions() {
        return Collections.singletonList(explanationConfiguration);
    }
}
