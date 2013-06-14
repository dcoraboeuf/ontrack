package net.ontrack.core.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ExtensionNode {

    private final String name;
    private final Set<String> dependencies;
    private final Set<String> requirementFor;

    public ExtensionNode(String name) {
        this(name, new HashSet<String>(), new HashSet<String>());
    }

    public void dependsOn(ExtensionNode extension) {
        dependencies.add(extension.getName());
        extension.getRequirementFor().add(this.getName());

    }
}
