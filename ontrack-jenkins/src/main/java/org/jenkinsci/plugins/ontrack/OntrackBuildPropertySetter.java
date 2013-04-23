package org.jenkinsci.plugins.ontrack;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import net.ontrack.client.ManageUIClient;
import net.ontrack.client.PropertyUIClient;
import net.ontrack.core.model.Ack;
import net.ontrack.core.model.BuildSummary;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.PropertyForm;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * Allows to notify for a build.
 */
public class OntrackBuildPropertySetter extends AbstractOntrackNotifier {

    private final String project;
    private final String branch;
    private final String build;
    private final String extension;
    private final String property;
    private final String value;

    @DataBoundConstructor
    public OntrackBuildPropertySetter(String project, String branch, String build, String extension, String property, String value) {
        this.project = project;
        this.branch = branch;
        this.build = build;
        this.extension = extension;
        this.property = property;
        this.value = value;
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

    public String getExtension() {
        return extension;
    }

    public String getProperty() {
        return property;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean perform(final AbstractBuild<?, ?> theBuild, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        // Only triggers in case of success
        if (theBuild.getResult().isBetterOrEqualTo(Result.SUCCESS)) {
            // Expands the expressions into actual values
            final String projectName = expand(project, theBuild, listener);
            final String branchName = expand(branch, theBuild, listener);
            final String buildName = expand(build, theBuild, listener);
            final String extensionName = expand(extension, theBuild, listener);
            final String propertyName = expand(property, theBuild, listener);
            final String theValue = expand(value, theBuild, listener);
            // Logging of parameters
            listener.getLogger().format(
                    "Setting property %s/%s = %s on build %s for branch %s of project %s%n",
                    extensionName, propertyName, theValue,
                    buildName, branchName, projectName);
            // Gets the build summary
            final BuildSummary buildSummary = OntrackClient.manage(new ManageClientCall<BuildSummary>() {
                @Override
                public BuildSummary onCall(ManageUIClient ui) {
                    return ui.getBuild(projectName, branchName, buildName);
                }
            });
            // Setting the property
            OntrackClient.property(new PropertyClientCall<Ack>() {
                @Override
                public Ack onCall(PropertyUIClient ui) {
                    return ui.saveProperty(Entity.BUILD, buildSummary.getId(),
                            extensionName, propertyName,
                            new PropertyForm(theValue));
                }
            });
        }
        // OK
        return true;
    }

    @Extension
    public static final class OntrackBuildPropertySetterDescriptorImpl extends BuildStepDescriptor<Publisher> {

        public OntrackBuildPropertySetterDescriptorImpl() {
            super(OntrackBuildPropertySetter.class);
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Ontrack: Build property setter";
        }
    }
}
