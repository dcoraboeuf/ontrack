package net.ontrack.core.security;

public interface AuthorizationUtils {

    void checkProject(int project, ProjectFunction fn);

    void checkBranch(int branch, ProjectFunction fn);

    void checkBuild(int build, ProjectFunction fn);

    void checkPromotionLevel(int promotionLevel, ProjectFunction fn);

    void checkValidationRun(int validationRun, ProjectFunction fn);

    void checkValidationStamp(int validationStamp, ProjectFunction fn);
}
