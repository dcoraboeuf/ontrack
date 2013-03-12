package net.ontrack.client.support;

import net.ontrack.client.ControlUIClient;
import net.ontrack.core.model.*;

import static java.lang.String.format;

public class DefaultControlUIClient extends AbstractClient implements ControlUIClient {

    public DefaultControlUIClient(String url) {
        super(url);
    }

    @Override
    public BuildSummary createBuild(String project, String branch, BuildCreationForm build) {
        return post(format("/ui/control/project/%s/branch/%s/build", project, branch), BuildSummary.class, build);
    }

    @Override
    public ValidationRunSummary createValidationRun(String project, String branch, String build, String validationStamp, ValidationRunCreationForm validationRun) {
        return post(format("/ui/control/project/%s/branch/%s/build/%s/validation_stamp/%s", project, branch, validationStamp, build), ValidationRunSummary.class, validationRun);
    }

    @Override
    public PromotedRunSummary createPromotedRun(String project, String branch, String build, String promotionLevel, PromotedRunCreationForm form) {
        return post(format("/ui/control/project/%s/branch/%s/build/%s/promotion_level/%s", project, branch, build, promotionLevel), PromotedRunSummary.class, form);
    }
}
