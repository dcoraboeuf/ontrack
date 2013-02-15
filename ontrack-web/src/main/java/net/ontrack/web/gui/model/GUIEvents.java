package net.ontrack.web.gui.model;

import lombok.Data;

import java.util.List;

@Data
public class GUIEvents {

    private final List<GUIEvent> events;
    private final boolean more;

}
