package net.ontrack.core.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ExtensionSummary {

    private final String name;
    private final String displayName;
    private final Set<String> dependencies;
    private final Set<String> requirementFor;

    public ExtensionSummary(String name, String displayName) {
        this(name, displayName, new HashSet<String>(), new HashSet<String>());
    }

    public void dependsOn(ExtensionSummary extension) {
        dependencies.add(extension.getName());
        extension.getRequirementFor().add(this.getName());

    }
}
