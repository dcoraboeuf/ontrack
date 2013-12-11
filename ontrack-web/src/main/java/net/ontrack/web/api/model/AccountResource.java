package net.ontrack.web.api.model;

import lombok.Data;
import net.ontrack.core.security.GlobalFunction;
import net.ontrack.core.security.ProjectACL;

import java.util.Locale;
import java.util.Set;

@Data
public class AccountResource extends AbstractResource<AccountResource> {

    private final int id;
    private final String name;
    private final String fullName;
    private final String email;
    private final String roleName;
    private final String mode;
    private final Locale locale;
    private final Set<GlobalFunction> globalACL;
    private final Set<ProjectACL> projectACL;

}
