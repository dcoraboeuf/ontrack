package net.ontrack.backend.security;

import net.ontrack.core.security.ProjectFunction;
import net.ontrack.core.security.ProjectGrant;
import net.ontrack.core.security.ProjectGrantId;

public class SampleImpl implements SampleAPI {

    @Override
    public void no_constraint() {
    }

    @Override
    @ProjectGrant(ProjectFunction.PROJECT_MODIFY)
    public void project_call_missing_param(int project) {
    }

    @Override
    @ProjectGrant(ProjectFunction.PROJECT_MODIFY)
    public void project_call_too_much(@ProjectGrantId int project, @ProjectGrantId int additional) {
    }

    @Override
    @ProjectGrant(ProjectFunction.PROJECT_MODIFY)
    public void project_call_ok(@ProjectGrantId int project, String name) {
    }
}
