package org.jenkinsci.plugins.ontrack;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import org.kohsuke.stapler.DataBoundConstructor;

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

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        // TODO Expands the expressions into actual values
        // Logging of parameters
        listener.getLogger().format("Creating build %s on project %s for branch %s%n", build, project, branch);
        // FIXME Calling ontrack UI
        // OK
        return true;
    }

    @Extension
    public static final class OntrackBuildDescriptorImpl extends BuildStepDescriptor<Publisher> {

        public OntrackBuildDescriptorImpl() {
            super(OntrackBuildNotifier.class);
        }

        @Override
        public String getDisplayName() {
            return "ontrack Build creation";
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

    }
}
