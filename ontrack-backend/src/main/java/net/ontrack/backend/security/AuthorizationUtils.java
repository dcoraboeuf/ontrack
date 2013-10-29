package net.ontrack.backend.security;

import net.ontrack.core.security.ProjectFunction;

public interface AuthorizationUtils {

    void checkProject(int project, ProjectFunction fn);

    void checkBranch(int branch, ProjectFunction fn);

    void checkBuild(int build, ProjectFunction fn);
}
