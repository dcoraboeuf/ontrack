package org.jenkinsci.plugins.ontrack;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.ontrack.client.ManageUIClient;
import net.ontrack.core.model.BuildSummary;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * Plug-in that allows to inject the last build name of a branch into
 * an environment variable.
 */
public class OntrackLastBuildStep extends Builder {

    private final String project;
    private final String branch;
    private final String variable;

    @DataBoundConstructor
    public OntrackLastBuildStep(String project, String branch, String variable) {
        this.project = project;
        this.branch = branch;
        this.variable = variable;
    }

    public String getProject() {
        return project;
    }

    public String getBranch() {
        return branch;
    }

    public String getVariable() {
        return variable;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> theBuild, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        // Gets the last build
        BuildSummary lastBuild = OntrackClient.manage(new ManageClientCall<BuildSummary>() {
            @Override
            public BuildSummary onCall(ManageUIClient ui) {
                return ui.getLastBuild(project, branch);
            }
        });
        // Found
        if (lastBuild != null) {
            String name = lastBuild.getName();
            listener.getLogger().format("Found build %s for branch %s and project %s%n", name, branch, project);
            theBuild.addAction(new ParametersAction(new StringParameterValue(variable, name)));
        }
        // Not found
        else {
            listener.getLogger().format("Could not find any build for branch %s and project %s%n", branch, project);
            theBuild.setResult(Result.FAILURE);
        }
        // OK
        return true;
    }

    @Extension
    public static class OntrackLastBuildStepDescription extends BuildStepDescriptor<Builder> {


        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "ontrack Last build";
        }
    }
}
