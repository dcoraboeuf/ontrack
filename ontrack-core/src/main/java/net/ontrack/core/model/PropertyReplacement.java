package net.ontrack.core.model;

import lombok.Data;

@Data
public class PropertyReplacement {

    private final String extension;
    private final String name;
    private final String regex;
    private final String replacement;

}
