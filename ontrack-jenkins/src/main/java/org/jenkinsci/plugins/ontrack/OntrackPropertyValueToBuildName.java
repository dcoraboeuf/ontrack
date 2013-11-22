package org.jenkinsci.plugins.ontrack;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.ontrack.client.PropertyUIClient;
import net.ontrack.client.support.PropertyClientCall;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.EntityStub;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.Collection;

import static org.jenkinsci.plugins.ontrack.OntrackPluginSupport.expand;

/**
 * This plug-in allows to get a build name with a given property value.
 */
public class OntrackPropertyValueToBuildName extends Builder {
    private final String project;
    private final String branch;
    private final String variable;
    private final String extension;
    private final String name;
    private final String propertyValue;

    @DataBoundConstructor
    public OntrackPropertyValueToBuildName(String project, String branch, String variable, String extension,
                                           String name, String propertyValue) {
        this.project = project;
        this.branch = branch;
        this.variable = variable;
        this.extension = extension;
        this.name = name;
        this.propertyValue = propertyValue;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> theBuild, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        // Expands the expressions into actual values
        // FIXME Takes into account the project & the branch
        /**
         * The query on property only could return a build that does not belong to
         * the project & branch. The filtering can be achieved by looping over
         * the build names & checking if they are actually defined for the given
         * project & branch.
         */
        final String projectName = expand(project, theBuild, listener);
        final String branchName = expand(branch, theBuild, listener);
        final String extensionName = expand(extension, theBuild, listener);
        final String propertyName = expand(name, theBuild, listener);
        final String expandedPropertyValue = expand(propertyValue, theBuild, listener);
        // Calls the UI
        Collection<EntityStub> entities = OntrackClient.property(new PropertyClientCall<Collection<EntityStub>>() {
            @Override
            public Collection<EntityStub> onCall(PropertyUIClient ui) {
                return ui.getEntitiesForPropertyValue(Entity.BUILD,
                        extensionName,
                        propertyName,
                        expandedPropertyValue);
            }
        });

        if (entities.size() == 1) {
            // Success case
            EntityStub entity = (EntityStub) entities.toArray()[0];
            theBuild.addAction(new ParametersAction(new StringParameterValue(variable, entity.getName())));
        } else {
            if (entities.size() == 0) {
                listener.getLogger().format("No build found with property '%s' '%s' value '%s'", extensionName, propertyName, expandedPropertyValue);
            } else {
                listener.getLogger().format("Multiple builds found with property '%s' '%s' value '%s'", extensionName, propertyName, expandedPropertyValue);
            }
            theBuild.setResult(Result.FAILURE);
        }

        return true;
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

    public String getExtension() {
        return extension;
    }

    public String getName() {
        return name;
    }

    @Extension
    public static class OntrackBuildPropertyValueDescription extends BuildStepDescriptor<Builder> {


        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Ontrack: Get build name by property value";
        }
    }
}
