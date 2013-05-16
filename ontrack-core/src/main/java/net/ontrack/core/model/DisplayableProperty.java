package net.ontrack.core.model;

import lombok.Data;

@Data
public class DisplayableProperty {

    private final String extension;
    private final String name;
    private final String displayName;
    private final String iconPath;

}
