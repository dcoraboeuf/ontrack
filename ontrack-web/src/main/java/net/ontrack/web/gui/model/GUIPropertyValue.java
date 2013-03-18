package net.ontrack.web.gui.model;

import lombok.Data;
import net.ontrack.extension.api.PropertyValueWithDescriptor;

@Data
public class GUIPropertyValue {

    private final PropertyValueWithDescriptor property;
    private final boolean editable;

}
