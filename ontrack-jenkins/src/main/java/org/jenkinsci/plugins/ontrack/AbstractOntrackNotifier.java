package org.jenkinsci.plugins.ontrack;

import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import jenkins.model.Jenkins;
import net.ontrack.client.ControlUIClient;
import net.ontrack.client.support.ClientFactory;
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

    protected <T> T call(ClientCall<T> clientCall) {
        // Gets the configuration
        OntrackConfiguration configuration = (OntrackConfiguration) Jenkins.getInstance().getDescriptor(OntrackConfiguration.class);
        // Gets the configuration data
        String url = configuration.getOntrackUrl();
        String user = configuration.getOntrackUser();
        String password = configuration.getOntrackPassword();
        // Controls the data
        if (StringUtils.isBlank(url)) {
            throw new IllegalStateException("ontrack URL global parameter must be defined");
        }
        if (StringUtils.isBlank(user)) {
            throw new IllegalStateException("ontrack User global parameter must be defined");
        }
        if (StringUtils.isBlank(password)) {
            throw new IllegalStateException("ontrack Password global parameter must be defined");
        }
        // Creates the client
        ControlUIClient client = ClientFactory.create(url).control();
        // Login
        client.login(user, password);
        try {
            // Performs the call
            return clientCall.onCall(client);
        } finally {
            client.logout();
        }
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
        String value = (String) theBuild.getBuildVariableResolver().resolve(name);
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
