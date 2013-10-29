package net.ontrack.backend.security;

import net.ontrack.backend.dao.BranchDao;
import net.ontrack.backend.dao.BuildDao;
import net.ontrack.core.security.GlobalFunction;
import net.ontrack.core.security.ProjectFunction;
import net.ontrack.core.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationUtilsImpl implements AuthorizationUtils {

    private final SecurityUtils securityUtils;
    private final BranchDao branchDao;
    private final BuildDao buildDao;

    @Autowired
    public AuthorizationUtilsImpl(SecurityUtils securityUtils, BranchDao branchDao, BuildDao buildDao) {
        this.securityUtils = securityUtils;
        this.branchDao = branchDao;
        this.buildDao = buildDao;
    }

    @Override
    public void checkGlobal(GlobalFunction fn) {
        securityUtils.checkGrant(fn);
    }

    @Override
    public void checkProject(int project, ProjectFunction fn) {
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
}
