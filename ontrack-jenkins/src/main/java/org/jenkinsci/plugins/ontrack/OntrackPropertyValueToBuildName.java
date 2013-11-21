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
        Collection<EntityStub> entities = OntrackClient.property(new PropertyClientCall<Collection<EntityStub>>() {
            @Override
            public Collection<EntityStub> onCall(PropertyUIClient ui) {
                return ui.getEntitiesForPropertyValue(Entity.BUILD,
                        extension,
                        name,
                        propertyValue);
            }
        });

        if (entities.size() == 1) {
            // Success case
            EntityStub entity = (EntityStub) entities.toArray()[0];
            theBuild.addAction(new ParametersAction(new StringParameterValue(variable, entity.getName())));
        } else {
            if (entities.size() == 0) {
                listener.getLogger().format("No build found with property '%s' '%s' value '%s'", extension, name, propertyValue);
            } else {
                listener.getLogger().format("Multiple builds found with property '%s' '%s' value '%s'", extension, name, propertyValue);
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
