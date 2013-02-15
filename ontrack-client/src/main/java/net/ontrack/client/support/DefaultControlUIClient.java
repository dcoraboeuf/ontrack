package net.ontrack.client.support;

import net.ontrack.client.ControlUIClient;
import net.ontrack.core.model.BuildCreationForm;
import net.ontrack.core.model.BuildSummary;
import net.ontrack.core.model.ValidationRunCreationForm;
import net.ontrack.core.model.ValidationRunSummary;

import static java.lang.String.format;

public class DefaultControlUIClient extends AbstractClient implements ControlUIClient {

    public DefaultControlUIClient(String url) {
        super(url);
    }

    @Override
    public BuildSummary createBuild(String project, String branch, BuildCreationForm build) {
        return post(format("/ui/control/build/%s/%s", project, branch), BuildSummary.class, build);
    }

    @Override
    public ValidationRunSummary createValidationRun(String project, String branch, String build, String validationStamp, ValidationRunCreationForm validationRun) {
        return post(format("/ui/control/validation/%s/%s/%s/%s", project, branch, validationStamp, build), ValidationRunSummary.class, validationRun);
    }
}
