package org.jenkinsci.plugins.ontrack;

import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import jenkins.model.Jenkins;
import net.ontrack.client.ControlUIClient;
import net.ontrack.client.support.ControlUIClientFactory;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractOntrackNotifier extends Notifier {

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
        ControlUIClient client = ControlUIClientFactory.create(url).build();
        // Login
        client.login(user, password);
        try {
            // Performs the call
            return clientCall.onCall(client);
        } finally {
            client.logout();
        }
    }

}
