package net.ontrack.web.gui.model;

import lombok.Data;

@Data
public class GUIConfigurationExtensionField {

    private final String name;
    private final String displayName;
    private final String type;
    private final String defaultValue;
    private final String value;

}
