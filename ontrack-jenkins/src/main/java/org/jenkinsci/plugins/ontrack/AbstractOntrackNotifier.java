package org.jenkinsci.plugins.ontrack;

import hudson.model.AbstractBuild;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import jenkins.model.Jenkins;
import net.ontrack.client.support.ControlClientCall;

public abstract class AbstractOntrackNotifier extends Notifier {

    /**
     * Name of the 'jenkins' extension for ontrack
     */
    public static final String EXTENSION_JENKINS = "jenkins";

    /**
     * Name of the 'url' property for the 'jenkins' extension in ontrack
     */
    public static final String PROPERTY_URL = "url";

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    protected String getBuildUrl(AbstractBuild<?, ?> theBuild) {
        return Jenkins.getInstance().getRootUrl() + theBuild.getUrl();
    }

    protected <T> T call(ControlClientCall<T> controlClientCall) {
        return OntrackClient.control(controlClientCall);
    }

}
