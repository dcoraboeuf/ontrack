package net.ontrack.extension.jenkins;

import net.ontrack.extension.api.configuration.ConfigurationExtension;
import net.ontrack.extension.api.support.ExtensionAdapter;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class JenkinsExtension extends ExtensionAdapter {

    public static final String EXTENSION = "jenkins";

    public JenkinsExtension() {
        super(
                EXTENSION,
                Collections.singletonList(new JenkinsUrlPropertyDescriptor()),
                Collections.<ConfigurationExtension>emptyList());
    }
}
