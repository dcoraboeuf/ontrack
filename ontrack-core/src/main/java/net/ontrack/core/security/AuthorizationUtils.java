package net.ontrack.core.security;

import net.ontrack.core.model.Entity;

public interface AuthorizationUtils {

    void checkBranch(int branch, ProjectFunction fn);

    void checkBuild(int build, ProjectFunction fn);

    void checkPromotionLevel(int promotionLevel, ProjectFunction fn);

    void checkValidationRun(int validationRun, ProjectFunction fn);

    void checkValidationStamp(int validationStamp, ProjectFunction fn);

    boolean applyPolicy(AuthorizationPolicy policy, Entity entity, int entityId);
}
