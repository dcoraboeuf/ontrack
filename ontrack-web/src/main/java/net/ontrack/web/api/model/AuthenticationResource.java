package net.ontrack.web.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class AuthenticationResource extends AbstractResource<AuthenticationResource> {

    private final int id;
    private final String name;
    private final String fullName;
    /**
     * List of functions available to the user in its contextual menu
     */
    private final List<ActionResource> actions;

}
