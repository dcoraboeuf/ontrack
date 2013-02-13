package org.jenkinsci.plugins.ontrack;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;

/**
 * Allows to notify for a build.
 */
public class OntrackBuildNotifier extends AbstractOntrackNotifier {

    private final String project;
    private final String branch;
    private final String build;

    @DataBoundConstructor
    public OntrackBuildNotifier(String project, String branch, String build) {
        this.project = project;
        this.branch = branch;
        this.build = build;
    }

    public String getProject() {
        return project;
    }

    public String getBranch() {
        return branch;
    }

    public String getBuild() {
        return build;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> theBuild, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        // TODO Expands the expressions into actual values
        // Logging of parameters
        listener.getLogger().format("Creating build %s on project %s for branch %s%n", build, project, branch);
        // FIXME Calling ontrack UI
        // OK
        return true;
    }

    @Override
    public OntrackBuildDescriptorImpl getDescriptor() {
        return (OntrackBuildDescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class OntrackBuildDescriptorImpl extends BuildStepDescriptor<Publisher> {

        private String ontrackUrl;
        private String ontrackUser;
        private String ontrackPassword;

        public OntrackBuildDescriptorImpl() {
            super(OntrackBuildNotifier.class);
            load();
        }

        @Override
        public String getDisplayName() {
            return "ontrack Build creation";
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
