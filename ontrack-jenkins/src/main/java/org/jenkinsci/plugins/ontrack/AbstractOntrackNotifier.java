package org.jenkinsci.plugins.ontrack;

import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import net.ontrack.client.ControlUIClient;
import net.ontrack.client.support.ControlUIClientFactory;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.stapler.StaplerRequest;

public abstract class AbstractOntrackNotifier extends Notifier {

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    protected <T> T call(ClientCall<T> clientCall) {
        // Gets the configuration
        OntrackDescriptorImpl descriptor = getDescriptor();
        // Gets the configuration data
        String url = descriptor.getOntrackUrl();
        String user = descriptor.getOntrackUser();
        String password = descriptor.getOntrackPassword();
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

    @Override
    public OntrackDescriptorImpl getDescriptor() {
        return (OntrackDescriptorImpl) super.getDescriptor();
    }

    public static abstract class OntrackDescriptorImpl extends BuildStepDescriptor<Publisher> {

        private String ontrackUrl;
        private String ontrackUser;
        private String ontrackPassword;

        public OntrackDescriptorImpl(Class<? extends AbstractOntrackNotifier> notifier) {
            super(notifier);
            load();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            ontrackUrl = json.getString("ontrackUrl");
            ontrackUser = json.getString("ontrackUser");
            ontrackPassword = json.getString("ontrackPassword");
            save();
            return super.configure(req, json);
        }

        public String getOntrackUrl() {
            return ontrackUrl;
        }

        public String getOntrackUser() {
            return ontrackUser;
        }

        public String getOntrackPassword() {
            return ontrackPassword;
        }
    }

}
