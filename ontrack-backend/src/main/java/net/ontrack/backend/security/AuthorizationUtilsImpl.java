package net.ontrack.backend.security;

import net.ontrack.backend.dao.*;
import net.ontrack.core.model.Entity;
import net.ontrack.core.security.AuthorizationPolicy;
import net.ontrack.core.security.AuthorizationUtils;
import net.ontrack.core.security.ProjectFunction;
import net.ontrack.core.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationUtilsImpl implements AuthorizationUtils {

    private final SecurityUtils securityUtils;
    private final BranchDao branchDao;
    private final BuildDao buildDao;
    private final PromotionLevelDao promotionLevelDao;
    private final ValidationStampDao validationStampDao;
    private final ValidationRunDao validationRunDao;
    private final EntityDao entityDao;

    @Autowired
    public AuthorizationUtilsImpl(SecurityUtils securityUtils, BranchDao branchDao, BuildDao buildDao, PromotionLevelDao promotionLevelDao, ValidationStampDao validationStampDao, ValidationRunDao validationRunDao, EntityDao entityDao) {
        this.securityUtils = securityUtils;
        this.branchDao = branchDao;
        this.buildDao = buildDao;
        this.promotionLevelDao = promotionLevelDao;
        this.validationStampDao = validationStampDao;
        this.validationRunDao = validationRunDao;
        this.entityDao = entityDao;
    }

    protected void checkProject(int project, ProjectFunction fn) {
        securityUtils.checkGrant(fn, project);
    }

    @Override
    public void checkBranch(int branch, ProjectFunction fn) {
        checkProject(branchDao.getById(branch).getProject(), fn);
    }

    @Override
    public void checkBuild(int build, ProjectFunction fn) {
        checkBranch(buildDao.getById(build).getBranch(), fn);
    }

    @Override
    public void checkPromotionLevel(int promotionLevel, ProjectFunction fn) {
        checkBranch(promotionLevelDao.getById(promotionLevel).getBranch(), fn);
    }

    @Override
    public void checkValidationRun(int validationRun, ProjectFunction fn) {
        checkBuild(validationRunDao.getById(validationRun).getBuild(), fn);
    }

    @Override
    public void checkValidationStamp(int validationStamp, ProjectFunction fn) {
        checkBranch(validationStampDao.getById(validationStamp).getBranch(), fn);
    }

    @Override
    public boolean applyPolicy(AuthorizationPolicy policy, Entity entity, int entityId) {
        if (policy.isAllowAll()) {
            return true;
        } else if (policy.getGlobalFn() != null) {
            return securityUtils.isGranted(policy.getGlobalFn());
        } else if (policy.getProjectFn() != null) {
            // Gets the project from the entity
            int projectId = getProjectId(entity, entityId);
            // Asserts the project function
            return securityUtils.isGranted(policy.getProjectFn(), projectId);
        } else {
            return false;
        }
    }

    private int getProjectId(Entity entity, int entityId) {
        return entityDao.parentLookup(Entity.PROJECT, entity, entityId);
    }
}
