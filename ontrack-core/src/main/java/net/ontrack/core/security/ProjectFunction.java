package net.ontrack.core.security;

public enum ProjectFunction {

    // ACL
    ACL,
    // Modify project
    PROJECT_MODIFY,
    // Delete project
    PROJECT_DELETE,
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
    PROMOTION_LEVEL_MGT,
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
    VALIDATION_STAMP_CREATE,
    // Modify validation stamp
    VALIDATION_STAMP_MODIFY,
    // Delete validation stamp
    VALIDATION_STAMP_DELETE,
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
    VALIDATION_RUN_DELETE,
    // Update validation run status
    VALIDATION_RUN_STATUS_MODIFY
}
