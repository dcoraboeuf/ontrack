package org.jenkinsci.plugins.ontrack;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.ontrack.client.ManageUIClient;
import net.ontrack.client.PropertyUIClient;
import net.ontrack.client.support.ManageClientCall;
import net.ontrack.client.support.PropertyClientCall;
import net.ontrack.core.model.BuildSummary;
import net.ontrack.core.model.Entity;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

public class OntrackBuildPropertyValue extends Builder {
    private final String project;
    private final String branch;
    private final String promotionLevel;
    private final String variable;
    private final String extension;
    private final String name;
    private final String buildName;
    private final boolean failOnBlank;

    @DataBoundConstructor
    public OntrackBuildPropertyValue(String project, String branch, String variable, String promotionLevel,
                                     String extension, String name, String buildName, boolean failOnBlank) {
        this.project = project;
        this.branch = branch;
        this.promotionLevel = promotionLevel;
        this.variable = variable;
        this.extension = extension;
        this.name = name;
        this.buildName = buildName;
        this.failOnBlank = failOnBlank;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> theBuild, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        final String actualProject = OntrackPluginSupport.expand(project, theBuild, listener);
        final String actualBranch = OntrackPluginSupport.expand(branch, theBuild, listener);
        final String actualPromotionLevel = OntrackPluginSupport.expand(promotionLevel, theBuild, listener);
        final String actualBuildName = OntrackPluginSupport.expand(buildName, theBuild, listener);
        final String actualExtension = OntrackPluginSupport.expand(extension, theBuild, listener);
        final String actualPropertyName = OntrackPluginSupport.expand(name, theBuild, listener);

        final BuildSummary lastBuild;
        if (StringUtils.isEmpty(buildName)) {
            // Get build from parameters
            lastBuild = OntrackClient.manage(new ManageClientCall<BuildSummary>() {
                public BuildSummary onCall(ManageUIClient ui) {
                    return ui.getLastBuildWithPromotionLevel(null, actualProject, actualBranch, actualPromotionLevel);
                }
            });
        } else {
            lastBuild = OntrackClient.manage(new ManageClientCall<BuildSummary>() {
                public BuildSummary onCall(ManageUIClient ui) {
                    return ui.getBuild(actualProject, actualBranch, actualBuildName);
                }
            });
        }

        if (lastBuild != null) {
            // If not null, get property package from that build
            String propertyValue = OntrackClient.property(new PropertyClientCall<String>() {
                public String onCall(PropertyUIClient ui) {
                    return ui.getPropertyValue(Entity.BUILD, lastBuild.getId(), actualExtension, actualPropertyName);
                }
            });

            if (StringUtils.isNotEmpty(propertyValue) || !failOnBlank) {
                // If not null, set this to the assigned build variable
                theBuild.addAction(new ParametersAction(new StringParameterValue(variable, propertyValue)));
            } else {
                // Unfortunatelly, the build requires this, so we have to fail the build
                listener.getLogger().format("Could not find any property for %s:%s in build %s", actualExtension, actualPropertyName, lastBuild.getId());
                theBuild.setResult(Result.FAILURE);
            }
        } else {
            listener.getLogger().format("Could not find any build for branch '%s' and project '%s' and promotion level '%s'%n", actualProject, actualBranch, actualPromotionLevel);
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

    public String getPromotionLevel() {
        return promotionLevel;
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

    public String getBuildName() {
        return buildName;
    }

    public boolean isFailOnBlank() {
        return failOnBlank;
    }

    @Extension
    public static class OntrackBuildPropertyValueDescription extends BuildStepDescriptor<Builder> {


        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Ontrack: Get build property value";
        }
    }
}
