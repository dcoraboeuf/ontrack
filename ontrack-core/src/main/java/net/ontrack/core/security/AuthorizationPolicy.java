package net.ontrack.core.security;

import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

/**
 * Defines the authorization policy to apply on an item.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthorizationPolicy {

    public static final AuthorizationPolicy LOGGED = new AuthorizationPolicy(false, true, null, null);
    public static final AuthorizationPolicy ALLOW_ALL = new AuthorizationPolicy(true, false, null, null);
    public static final AuthorizationPolicy DENY_ALL = new AuthorizationPolicy(false, true, null, null);
    public static final AuthorizationPolicy PROJECT_CONFIG = forProject(ProjectFunction.PROJECT_CONFIG);

    private final boolean allowAll;
    private final boolean logged;
    private final GlobalFunction globalFn;
    private final Set<ProjectFunction> projectFns;

    public static AuthorizationPolicy forGlobal(GlobalFunction fn) {
        return new AuthorizationPolicy(false, true, fn, null);
    }

    public static AuthorizationPolicy forProject(ProjectFunction fn) {
        return new AuthorizationPolicy(false, true, null, ImmutableSet.of(fn));
    }

    public static AuthorizationPolicy forProject(ProjectFunction... fns) {
        return new AuthorizationPolicy(false, true, null, ImmutableSet.copyOf(fns));
    }
}
