package org.jenkinsci.plugins.ontrack;

import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import jenkins.model.Jenkins;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractOntrackNotifier extends Notifier {

    public static final String REGEX_ENV_VARIABLE = "\\$\\{([a-zA-Z0-9_]+)\\}";

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

    protected String expand(String template, AbstractBuild<?, ?> theBuild, BuildListener listener) {
        if (StringUtils.isBlank(template)) {
            return template;
        } else {
            Pattern pattern = Pattern.compile(REGEX_ENV_VARIABLE);
            Matcher matcher = pattern.matcher(template);
            StringBuffer result = new StringBuffer();
            while (matcher.find()) {
                String name = matcher.group(1);
                String value = getParameter(name, theBuild, listener);
                if (value == null) {
                    throw new IllegalStateException("Cannot find any replacement value for environment variable " + name);
                }
                matcher = matcher.appendReplacement(result, value);
            }
            matcher.appendTail(result);
            return result.toString();
        }
    }

    protected String getParameter(String name, AbstractBuild<?, ?> theBuild, BuildListener listener) {
        String value = theBuild.getBuildVariableResolver().resolve(name);
        if (value != null) {
            return value;
        } else {
            try {
                return theBuild.getEnvironment(listener).get(name);
            } catch (Exception ex) {
                throw new RuntimeException("Cannot get value for " + name, ex);
            }
        }
    }

}
