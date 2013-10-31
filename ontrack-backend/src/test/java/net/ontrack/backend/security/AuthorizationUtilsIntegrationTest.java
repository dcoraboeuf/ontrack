package net.ontrack.backend.security;

import net.ontrack.backend.AbstractBackendTest;
import net.ontrack.core.model.*;
import net.ontrack.core.security.AuthorizationPolicy;
import net.ontrack.core.security.AuthorizationUtils;
import net.ontrack.core.security.GlobalFunction;
import net.ontrack.core.security.ProjectFunction;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.concurrent.Callable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AuthorizationUtilsIntegrationTest extends AbstractBackendTest {

    @Autowired
    private AuthorizationUtils utils;

    @Test
    public void applyPolicy_allow_all() {
        for (Entity entity : Entity.values()) {
            assertTrue("Access to " + entity + " must be granted for 'allow_all' policy", utils.applyPolicy(AuthorizationPolicy.ALLOW_ALL, entity, 1));
        }
    }

    @Test
    public void applyPolicy_logged_for_not_logged() {
        SecurityContextHolder.clearContext();
        for (Entity entity : Entity.values()) {
            assertFalse("Access to " + entity + " must not be granted for 'logged' policy", utils.applyPolicy(AuthorizationPolicy.LOGGED, entity, 1));
        }
    }

    @Test
    public void applyPolicy_logged_for_logged() throws Exception {
        asUser().call(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (Entity entity : Entity.values()) {
                    assertTrue("Access to " + entity + " must be granted for 'logged' policy", utils.applyPolicy(AuthorizationPolicy.LOGGED, entity, 1));
                }
                return null;
            }
        });
    }

    @Test
    public void applyPolicy_global_granted() throws Exception {
        final AuthorizationPolicy policy = AuthorizationPolicy.forGlobal(GlobalFunction.EXTENSIONS);
        asUser().withGlobalFn(GlobalFunction.EXTENSIONS).call(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (Entity entity : Entity.values()) {
                    assertTrue("Access to " + entity + " must be granted for global function policy", utils.applyPolicy(policy, entity, 1));
                }
                return null;
            }
        });
    }

    @Test
    public void applyPolicy_global_not_granted() throws Exception {
        final AuthorizationPolicy policy = AuthorizationPolicy.forGlobal(GlobalFunction.EXTENSIONS);
        asUser().withGlobalFn(GlobalFunction.PROJECT_CREATE).call(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (Entity entity : Entity.values()) {
                    assertFalse("Access to " + entity + " must not be granted for global function policy not granted", utils.applyPolicy(policy, entity, 1));
                }
                return null;
            }
        });
    }

    @Test
    public void applyPolicy_project_at_project_level() throws Exception {
        final AuthorizationPolicy policy = AuthorizationPolicy.forProject(ProjectFunction.ACL);
        asUser().withProjectFn(ProjectFunction.ACL, 1).call(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                assertTrue("Access to project 1 must be granted", utils.applyPolicy(policy, Entity.PROJECT, 1));
                assertFalse("Access to project 2 must not be granted", utils.applyPolicy(policy, Entity.PROJECT, 2));
                return null;
            }
        });
    }

    @Test
    public void applyPolicy_project_at_branch_level() throws Exception {
        // Model
        final BranchSummary b1 = doCreateBranch();
        final BranchSummary b2 = doCreateBranch();
        // Authorization policy
        final AuthorizationPolicy policy = AuthorizationPolicy.forProject(ProjectFunction.ACL);
        // Authorization context
        asUser().withProjectFn(ProjectFunction.ACL, b1.getProject().getId()).call(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                assertTrue("Access to branch 1 must be granted", utils.applyPolicy(policy, Entity.BRANCH, b1.getId()));
                assertFalse("Access to branch 2 must not be granted", utils.applyPolicy(policy, Entity.BRANCH, b2.getId()));
                return null;
            }
        });
    }

    @Test
    public void applyPolicy_project_at_promotion_level_level() throws Exception {
        // Model
        final PromotionLevelSummary pl1 = doCreatePromotionLevel();
        final PromotionLevelSummary pl2 = doCreatePromotionLevel();
        // Authorization policy
        final AuthorizationPolicy policy = AuthorizationPolicy.forProject(ProjectFunction.ACL);
        // Authorization context
        asUser().withProjectFn(ProjectFunction.ACL, pl1.getBranch().getProject().getId()).call(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                assertTrue("Access to branch 1 must be granted", utils.applyPolicy(policy, Entity.PROMOTION_LEVEL, pl1.getId()));
                assertFalse("Access to branch 2 must not be granted", utils.applyPolicy(policy, Entity.PROMOTION_LEVEL, pl2.getId()));
                return null;
            }
        });
    }

    @Test
    public void applyPolicy_project_at_validation_run_level() throws Exception {
        // Model
        final ValidationRunSummary vr1 = doCreateValidationRun(Status.PASSED);
        final ValidationRunSummary vr2 = doCreateValidationRun(Status.PASSED);
        // Authorization policy
        final AuthorizationPolicy policy = AuthorizationPolicy.forProject(ProjectFunction.ACL);
        // Authorization context
        asUser().withProjectFn(ProjectFunction.ACL, vr1.getBuild().getBranch().getProject().getId()).call(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                assertTrue("Access to branch 1 must be granted", utils.applyPolicy(policy, Entity.VALIDATION_RUN, vr1.getId()));
                assertFalse("Access to branch 2 must not be granted", utils.applyPolicy(policy, Entity.VALIDATION_RUN, vr2.getId()));
                return null;
            }
        });
    }

    @Test
    public void applyPolicy_deny_all() {
        for (Entity entity : Entity.values()) {
            assertFalse("Access to " + entity + " must not be granted for 'deny_all' policy", utils.applyPolicy(AuthorizationPolicy.DENY_ALL, entity, 1));
        }
    }

}
