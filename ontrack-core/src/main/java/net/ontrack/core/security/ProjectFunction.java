package net.ontrack.core.security;

import com.google.common.collect.ImmutableSet;

import java.util.EnumSet;
import java.util.Set;

/**
 * See <code>model.ls</code> for the associated labels.
 */
public enum ProjectFunction {

    // ACL
    ACL,
    // Modify project
    PROJECT_MODIFY,
    // Delete project (admin only)
    PROJECT_DELETE(EnumSet.noneOf(ProjectRole.class)),
    // Create branch
    BRANCH_CREATE,
    // Create build
    BUILD_CREATE(EnumSet.of(ProjectRole.OWNER), ImmutableSet.of(SecurityRoles.CONTROLLER)),
    // Modify branch
    BRANCH_MODIFY,
    // Delete branch
    BRANCH_DELETE,
    // Clone branch
    BRANCH_CLONE,
    // Manage promotion levels
    PROMOTION_LEVEL_MGT(EnumSet.of(ProjectRole.OWNER, ProjectRole.VALIDATION_MANAGER)),
    // Clean-up configuration
    BUILD_CLEANUP_CONFIG,
    // Dashboard set-up
    DASHBOARD_SETUP,
    // Create promotion level
    PROMOTION_LEVEL_CREATE,
    // Modify promotion level
    PROMOTION_LEVEL_MODIFY,
    // Delete promotion level
    PROMOTION_LEVEL_DELETE,
    // Create validation stamp
    VALIDATION_STAMP_CREATE(EnumSet.of(ProjectRole.OWNER, ProjectRole.VALIDATION_MANAGER)),
    // Modify validation stamp
    VALIDATION_STAMP_MODIFY(EnumSet.of(ProjectRole.OWNER, ProjectRole.VALIDATION_MANAGER)),
    // Delete validation stamp
    VALIDATION_STAMP_DELETE(EnumSet.of(ProjectRole.OWNER, ProjectRole.VALIDATION_MANAGER)),
    // Modify build
    BUILD_MODIFY,
    // Delete build
    BUILD_DELETE,
    // Create promoted run
    PROMOTED_RUN_CREATE(EnumSet.of(ProjectRole.OWNER, ProjectRole.PROMOTER), ImmutableSet.of(SecurityRoles.CONTROLLER)),
    // Delete promoted run
    PROMOTED_RUN_DELETE(EnumSet.of(ProjectRole.OWNER, ProjectRole.PROMOTER)),
    // Create validation run
    VALIDATION_RUN_CREATE(EnumSet.of(ProjectRole.OWNER), ImmutableSet.of(SecurityRoles.CONTROLLER)),
    // Delete validation run
    VALIDATION_RUN_DELETE,
    // Configuration of a project
    PROJECT_CONFIG;
    /**
     * List of authorized project roles
     */
    private final EnumSet<ProjectRole> allowedProjectRoles;
    /**
     * List of authorized global roles
     */
    private final Set<String> allowedGlobalRoles;

    private ProjectFunction(EnumSet<ProjectRole> allowedProjectRoles, Set<String> allowedGlobalRoles) {
        this.allowedProjectRoles = allowedProjectRoles;
        this.allowedGlobalRoles = allowedGlobalRoles;
    }

    private ProjectFunction() {
        this(EnumSet.of(ProjectRole.OWNER));
    }

    private ProjectFunction(EnumSet<ProjectRole> allowedProjectRoles) {
        this(allowedProjectRoles, ImmutableSet.<String>of());
    }

    public boolean isAllowedForProjectRole(ProjectRole role) {
        return allowedProjectRoles.contains(role);
    }

    public boolean isAllowedForGlobalRole(String role) {
        return allowedGlobalRoles.contains(role);
    }
}
