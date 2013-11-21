package org.jenkinsci.plugins.ontrack;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import net.ontrack.client.ControlUIClient;
import net.ontrack.client.support.ControlClientCall;
import net.ontrack.core.model.*;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * Allows to notify for a build.
 */
public class OntrackValidationRunNotifier extends AbstractOntrackNotifier {

    private final String project;
    private final String branch;
    private final String build;
    private final String validationStamp;

    @DataBoundConstructor
    public OntrackValidationRunNotifier(String project, String branch, String build, String validationStamp) {
        this.project = project;
        this.branch = branch;
        this.build = build;
        this.validationStamp = validationStamp;
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

    public String getValidationStamp() {
        return validationStamp;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> theBuild, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        // Expands the expressions into actual values
        final String projectName = expand(project, theBuild, listener);
        final String branchName = expand(branch, theBuild, listener);
        final String buildName = expand(build, theBuild, listener);
        final String validationStampName = expand(validationStamp, theBuild, listener);
        // Run status
        Status runStatus = getRunStatus(theBuild);
        // TODO Run description
        String runDescription = String.format("Run %s", theBuild);
        // Run creation form
        final ValidationRunCreationForm runCreationForm = new ValidationRunCreationForm(
                runStatus,
                runDescription,
                PropertiesCreationForm.create().with(
                        new PropertyCreationForm(
                                EXTENSION_JENKINS, // Jenkins extension
                                PROPERTY_URL, // Jenkins related URL
                                getBuildUrl(theBuild) // URL to this build
                        )
                )
        );
        // Logging of parameters
        listener.getLogger().format("[ontrack] Running %s with status %s for build %s of branch %s of project %s%n", validationStampName, runStatus, buildName, branchName, projectName);
        // Calling ontrack UI
        ValidationRunSummary summary = call(new ControlClientCall<ValidationRunSummary>() {
            public ValidationRunSummary onCall(ControlUIClient ui) {
                return ui.createValidationRun(projectName, branchName, buildName, validationStampName, runCreationForm);
            }
        });
        // OK
        return true;
    }

    private Status getRunStatus(AbstractBuild<?, ?> theBuild) {
        Result result = theBuild.getResult();
        if (result.isBetterOrEqualTo(Result.SUCCESS)) {
            return Status.PASSED;
        } else if (result.isBetterOrEqualTo(Result.UNSTABLE)) {
            return Status.WARNING;
        } else if (result.equals(Result.ABORTED)) {
            return Status.INTERRUPTED;
        } else {
            return Status.FAILED;
        }
    }

    @Extension
    public static final class OntrackValidationRunDescriptorImpl extends BuildStepDescriptor<Publisher> {

        public OntrackValidationRunDescriptorImpl() {
            super(OntrackValidationRunNotifier.class);
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Ontrack: Validation run creation";
        }
    }
}
