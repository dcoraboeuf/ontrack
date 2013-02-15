package org.jenkinsci.plugins.ontrack;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import net.ontrack.core.model.Status;
import net.ontrack.core.model.ValidationRunCreationForm;
import net.ontrack.core.model.ValidationRunSummary;
import net.ontrack.core.ui.ControlUI;
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
        // TODO Run status
        Status runStatus = Status.PASSED;
        // TODO Run description
        String runDescription = String.format("Run %s", theBuild);
        // Run creation form
        final ValidationRunCreationForm runCreationForm = new ValidationRunCreationForm(runStatus, runDescription);
        // Logging of parameters
        listener.getLogger().format("Running %s with status %s for build %s of branch %s of project %s", validationStampName, runStatus, buildName, branchName, projectName);
        // Calling ontrack UI
        ValidationRunSummary summary = call(new ClientCall<ValidationRunSummary>() {
            public ValidationRunSummary onCall(ControlUI ui) {
                return ui.createValidationRun(projectName, branchName, buildName, validationStampName, runCreationForm);
            }
        });
        // OK
        return true;
    }

    @Extension
    public static final class OntrackBuildDescriptorImpl extends BuildStepDescriptor<Publisher> {

        public OntrackBuildDescriptorImpl() {
            super(OntrackValidationRunNotifier.class);
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "ontrack Validation run creation";
        }
    }
}
