package net.ontrack.core.model;

import lombok.Data;

import java.util.Set;

@Data
public class ExtensionSummary {

    private final String name;
    private final String displayName;
    private final boolean enabled;
    private final Set<String> dependencies;
    private final Set<String> requirementFor;

}
