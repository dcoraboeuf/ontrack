package net.ontrack.extension.explanation;

import net.ontrack.extension.api.configuration.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class ExplanationConfiguration implements ConfigurationExtension {

    private List<String> explanations = Collections.emptyList();

    public List<String> getExplanations() {
        return explanations;
    }

    @Override
    public String getExtension() {
        return ExplanationExtension.EXTENSION;
    }

    @Override
    public String getName() {
        return "configuration";
    }

    @Override
    public String getTitleKey() {
        return "explanation.configuration";
    }

    @Override
    public List<? extends ConfigurationExtensionField> getFields() {
        return Collections.singletonList(
                new MemoConfigurationExtensionField("explanations", "explanation.configuration.list", "", toStringField())
        );
    }

    private String toStringField() {
        return StringUtils.join(explanations, "\n");
    }

    @Override
    public void configure(String name, String value) {
        switch (name) {
            case "explanations":
                String text = value == null ? "" : value;
                explanations = Arrays.asList(StringUtils.split(text, "\r\n"));
                break;
        }
    }
}
