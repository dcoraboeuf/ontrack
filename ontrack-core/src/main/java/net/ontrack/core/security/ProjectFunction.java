package net.ontrack.core.security;

import java.util.EnumSet;

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
    BUILD_CREATE,
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
    PROMOTED_RUN_CREATE,
    // Delete promoted run
    PROMOTED_RUN_DELETE,
    // Create validation run
    VALIDATION_RUN_CREATE,
    // Delete validation run
    VALIDATION_RUN_DELETE;
    /**
     * List of authorized roles
     */
    private final EnumSet<ProjectRole> allowedRoles;

    private ProjectFunction() {
        this(EnumSet.of(ProjectRole.OWNER));
    }

    private ProjectFunction(EnumSet<ProjectRole> allowedRoles) {
        this.allowedRoles = allowedRoles;
    }

    public boolean isAllowedForRole(ProjectRole role) {
        return allowedRoles.contains(role);
    }
}
