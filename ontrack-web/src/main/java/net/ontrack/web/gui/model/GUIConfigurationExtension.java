package net.ontrack.web.gui.model;

import lombok.Data;

import java.util.List;

@Data
public class GUIConfigurationExtension {

    private final String extension;
    private final String name;
    private final String title;
    private final List<GUIConfigurationExtensionField> fields;

}
