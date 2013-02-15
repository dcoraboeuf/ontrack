package net.ontrack.core.model;

import lombok.Data;

import java.util.List;

@Data
public class ExpandedEvents {

    private final List<ExpandedEvent> events;
    private final boolean more;

}
