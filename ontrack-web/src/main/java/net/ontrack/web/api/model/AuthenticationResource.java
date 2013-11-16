package net.ontrack.web.api.model;

import lombok.Data;

@Data
public class AuthenticationResource extends AbstractResource<AuthenticationResource> {

    private final int id;
    private final String name;
    private final String fullName;

}
