package net.ontrack.core.model;

import lombok.Data;

@Data
public class EditableProperty {

    private final String extension;
    private final String name;
    private final String displayName;
    private final String displayDescription;
    private final String iconPath;
    private final String value;
    private final String htmlValue;
    private final String htmlForEdit;

}
