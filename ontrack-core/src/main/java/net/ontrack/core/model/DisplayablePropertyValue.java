package net.ontrack.core.model;

import lombok.Data;

@Data
public class DisplayablePropertyValue {

    private final String html;
    private final String extension;
    private final String name;
    private final String displayName;
    private final String iconPath;
    private final String value;
    private final boolean editable;

}
