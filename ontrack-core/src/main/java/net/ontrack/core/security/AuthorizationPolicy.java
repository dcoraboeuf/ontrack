package net.ontrack.core.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Defines the authorization policy to apply on an item.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthorizationPolicy {

    public static final AuthorizationPolicy ALLOW_ALL = new AuthorizationPolicy(true, null, null);
    public static final AuthorizationPolicy DENY_ALL = new AuthorizationPolicy(false, null, null);

    private final boolean allowAll;
    private final GlobalFunction globalFn;
    private final ProjectFunction projectFn;

    public static AuthorizationPolicy forGlobal(GlobalFunction fn) {
        return new AuthorizationPolicy(false, fn, null);
    }

    public static AuthorizationPolicy forProject(ProjectFunction fn) {
        return new AuthorizationPolicy(false, null, fn);
    }
}
