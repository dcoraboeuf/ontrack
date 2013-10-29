package net.ontrack.core.security;

import net.ontrack.core.security.GlobalFunction;
import net.ontrack.core.security.ProjectFunction;

public interface AuthorizationUtils {

    void checkGlobal(GlobalFunction fn);

    void checkProject(int project, ProjectFunction fn);

    void checkBranch(int branch, ProjectFunction fn);

    void checkBuild(int build, ProjectFunction fn);

    void checkPromotionLevel(int promotionLevel, ProjectFunction fn);

    void checkValidationRun(int validationRun, ProjectFunction fn);

    void checkValidationStamp(int validationStamp, ProjectFunction fn);
}
