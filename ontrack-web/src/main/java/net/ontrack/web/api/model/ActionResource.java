package net.ontrack.web.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ActionResource extends AbstractResource<ActionResource> {

    private final String key;

}
