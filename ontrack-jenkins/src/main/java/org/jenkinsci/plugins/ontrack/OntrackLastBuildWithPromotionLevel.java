package org.jenkinsci.plugins.ontrack;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.ontrack.client.ManageUIClient;
import net.ontrack.client.support.ManageClientCall;
import net.ontrack.core.model.BuildSummary;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 */
public class OntrackLastBuildWithPromotionLevel extends Builder {

    private final String project;
    private final String branch;
	private final String promotionLevel;
    private final String variable;

    @DataBoundConstructor
    public OntrackLastBuildWithPromotionLevel(String project, String branch, String variable, String promotionLevel) {
        this.project = project;
        this.branch = branch;
        this.variable = variable;
		this.promotionLevel = promotionLevel;
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

	public String getPromotionLevel() {
		return promotionLevel;
	}

    @Override
    public boolean perform(AbstractBuild<?, ?> theBuild, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        final String actualProject = OntrackPluginSupport.expand(project, theBuild, listener);
        final String actualBranch = OntrackPluginSupport.expand(branch, theBuild, listener);
        final String actualPromotionLevel = OntrackPluginSupport.expand(promotionLevel, theBuild, listener);

        // Gets the last build
        BuildSummary lastBuild = OntrackClient.manage(new ManageClientCall<BuildSummary>() {
            @Override
            public BuildSummary onCall(ManageUIClient ui) {
				return ui.getLastBuildWithPromotionLevel(null, actualProject, actualBranch, actualPromotionLevel);
            }
        });
        // Found
        if (lastBuild != null) {
            String name = lastBuild.getName();
            listener.getLogger().format("Found build '%s' for branch '%s' and project '%s' and promotion level '%s'%n", name, actualBranch, actualProject, actualPromotionLevel);
            theBuild.addAction(new ParametersAction(new StringParameterValue(variable, name)));
        }
        // Not found
        else {
            listener.getLogger().format("Could not find any build for branch '%s' and project '%s' and promotion level '%s'%n", actualBranch, actualProject, actualPromotionLevel);
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
            return "Ontrack: Last build with promotion level";
        }
    }
}
