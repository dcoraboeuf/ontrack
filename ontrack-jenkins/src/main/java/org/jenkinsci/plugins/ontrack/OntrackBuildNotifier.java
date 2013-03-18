package org.jenkinsci.plugins.ontrack;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import net.ontrack.core.model.BuildCreationForm;
import net.ontrack.core.model.BuildSummary;
import net.ontrack.core.model.PropertiesCreationForm;
import net.ontrack.core.model.PropertyCreationForm;
import net.ontrack.core.ui.ControlUI;
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
    public boolean perform(final AbstractBuild<?, ?> theBuild, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        // Only triggers in case of success
        if (theBuild.getResult().isBetterOrEqualTo(Result.SUCCESS)) {
            // Expands the expressions into actual values
            final String projectName = expand(project, theBuild, listener);
            final String branchName = expand(branch, theBuild, listener);
            final String buildName = expand(build, theBuild, listener);
            // TODO Build description
            final String buildDescription = String.format("Build %s", theBuild);
            // Logging of parameters
            listener.getLogger().format("Creating build %s on project %s for branch %s%n", buildName, projectName, branchName);
            // Calling ontrack UI
            BuildSummary buildSummary = call(new ControlClientCall<BuildSummary>() {
                public BuildSummary onCall(ControlUI ui) {
                    return ui.createBuild(
                            projectName,
                            branchName,
                            new BuildCreationForm(
                                    buildName,
                                    buildDescription,
                                    PropertiesCreationForm.create().with(
                                            new PropertyCreationForm(
                                                    EXTENSION_JENKINS, // Jenkins extension
                                                    PROPERTY_URL, // Jenkins related URL
                                                    getBuildUrl(theBuild) // URL to this build
                                            )
                                    )
                                    ));
                }
            });
        }
        // OK
        return true;
    }

    @Extension
    public static final class OntrackBuildDescriptorImpl extends BuildStepDescriptor<Publisher> {

        public OntrackBuildDescriptorImpl() {
            super(OntrackBuildNotifier.class);
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "ontrack Build creation";
        }
    }
}
