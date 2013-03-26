package net.ontrack.extension.explanation;

import net.ontrack.extension.api.support.ExtensionAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class ExplanationExtension extends ExtensionAdapter {

    public static final String EXTENSION = "explanation";

    @Autowired
    public ExplanationExtension(ExplanationPropertyExtension property, ExplanationConfiguration configuration) {
        super(
                EXTENSION,
                Arrays.asList(property),
                Arrays.asList(configuration));
    }
}
