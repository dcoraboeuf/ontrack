package net.ontrack.web.ui.model;

import lombok.Data;

@Data
public class DisplayablePropertyValue {

    private final String html;
    private final String extension;
    private final String name;
    private final String value;
    private final boolean editable;

}
