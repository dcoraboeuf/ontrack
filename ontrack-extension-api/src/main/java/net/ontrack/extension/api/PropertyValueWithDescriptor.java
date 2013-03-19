package net.ontrack.extension.api;

import lombok.Data;

@Data
public class PropertyValueWithDescriptor {

    private final PropertyExtensionDescriptor descriptor;
    private final String value;

}
