package net.ontrack.extension.jenkins;

import net.ontrack.extension.api.configuration.ConfigurationExtension;
import net.ontrack.extension.api.configuration.ConfigurationExtensionField;
import net.ontrack.extension.api.configuration.TextConfigurationExtensionField;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class JenkinsConfigurationExtension implements ConfigurationExtension {

    public static final String IMAGE_URL = "image.url";
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String getExtension() {
        return JenkinsExtension.EXTENSION;
    }

    @Override
    public String getName() {
        return "configuration";
    }

    @Override
    public String getTitleKey() {
        return "jenkins.configuration";
    }

    @Override
    public List<? extends ConfigurationExtensionField> getFields() {
        // Converts to fields
        return Collections.singletonList(
                new TextConfigurationExtensionField(IMAGE_URL, "jenkins.configuration.imageUrl", "", imageUrl)
        );
    }

    @Override
    public void configure(String name, String value) {
        switch (name) {
            case IMAGE_URL:
                imageUrl = value;
                break;
        }
    }
}
