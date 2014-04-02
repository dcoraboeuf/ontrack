package org.jenkinsci.plugins.ontrack;

import antlr.ANTLRException;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.*;
import net.ontrack.client.ManageUIClient;
import net.ontrack.client.support.ManageClientCall;
import net.ontrack.core.model.BuildSummary;
import net.ontrack.core.model.OptionalBuildSummary;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.lib.xtrigger.AbstractTrigger;
import org.jenkinsci.lib.xtrigger.XTriggerDescriptor;
import org.jenkinsci.lib.xtrigger.XTriggerException;
import org.jenkinsci.lib.xtrigger.XTriggerLog;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.*;

public class OntrackPromotionLevelPollingTrigger extends AbstractTrigger {
	private final String project;
	private final String branch;
	private final String promotionLevel;

	@DataBoundConstructor
	public OntrackPromotionLevelPollingTrigger(String cronTabSpec, String triggerLabel, String project, String branch,
												String promotionLevel) throws ANTLRException {
		super(cronTabSpec, triggerLabel);
		this.project = project;
		this.branch = branch;
		this.promotionLevel = promotionLevel;
	}

	@Override
	protected File getLogFile() {
		return new File(job.getRootDir(), "ontrack-promotionlevel-polling-trigger.log");
	}

	@Override
	protected boolean requiresWorkspaceForPolling() {
		return false;
	}

	@Override
	protected String getName() {
		return "Ontrack: Poll promotion level";
	}

	@Override
	protected Action[] getScheduledActions(Node node, XTriggerLog xTriggerLog) {
		return new Action[0];
	}

	@Override
	protected boolean checkIfModified(Node node, XTriggerLog xTriggerLog) throws XTriggerException {
		if (checkConfigs(xTriggerLog)) return false;

        String actualProject = resolveEnvVars(project, (AbstractProject) job, node);
        String actualBranch = resolveEnvVars(branch, (AbstractProject) job, node);
        String actualPromotionLevel = resolveEnvVars(promotionLevel, (AbstractProject) job, node);

		FilePath lastBuildNrFile = new FilePath(node.getRootPath(), String.format("%s-promotionlevel-lastBuildNr", job.getName()));
		String lastBuildNr = loadLastBuildNr(xTriggerLog, lastBuildNrFile);

		// Gets the last build
		BuildSummary lastBuild = getBuildSummary(actualProject, actualBranch, actualPromotionLevel);

		// Found
		if (lastBuild != null) {
			String name = lastBuild.getName();
			xTriggerLog.info(String.format("Found build '%s' for branch '%s' and project '%s' and promotion level '%s'%n", name, actualBranch, actualProject, actualPromotionLevel));
			try {
				if (lastBuildNr == null || lastBuildNr.isEmpty() || !lastBuildNr.equals(name)) {
					saveLastBuildNr(name, xTriggerLog, lastBuildNrFile);
					return true;
				}
			} catch (IOException e) {
				logException(xTriggerLog, e);
			} catch (InterruptedException e) {
				logException(xTriggerLog, e);
			}
		}

		return false;
	}

	@Override
	protected String getCause() {
		return String.format("New build found with promotion level %s", promotionLevel);
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

	private static void saveLastBuildNr(String lastBuildNr, XTriggerLog xTriggerLog, FilePath lastBuildNrFile) throws IOException, InterruptedException {
		lastBuildNrFile.write(lastBuildNr, "UTF-8");
		xTriggerLog.info(String.format("Wrote buildNr: %s", lastBuildNr));
	}

	private static String loadLastBuildNr(XTriggerLog xTriggerLog, FilePath lastBuildNrFile) {
		String lastBuildNr = null;

		try {
			if (lastBuildNrFile.exists()) {
				lastBuildNr = lastBuildNrFile.readToString();

				xTriggerLog.info(String.format("Loaded buildNr: %s", lastBuildNr));
			}
		} catch (IOException e) {
			logException(xTriggerLog, e);
		} catch (InterruptedException e) {
			logException(xTriggerLog, e);
		}

		return lastBuildNr;
	}

	private boolean checkConfigs(XTriggerLog xTriggerLog) {
		if (StringUtils.isEmpty(project)) {
			xTriggerLog.info("Ontrack: No project configured");
			return true;
		}

		if (StringUtils.isEmpty(branch)) {
			xTriggerLog.info("Ontrack: No branch configured");
			return true;
		}

		if (StringUtils.isEmpty(promotionLevel)) {
			xTriggerLog.info("Ontrack: No promotion level configured");
			return true;
		}
		return false;
	}

	private BuildSummary getBuildSummary(final String project, final String branch, final String promotionLevel) {
		return OntrackClient.manage(new ManageClientCall<OptionalBuildSummary>() {
			@Override
			public OptionalBuildSummary onCall(ManageUIClient ui) {
				return ui.getLastBuildWithPromotionLevel(null, project, branch, promotionLevel);
			}
		}).getBuild();
	}

	private static void logException(XTriggerLog xTriggerLog, Exception e) {
		e.printStackTrace();
		xTriggerLog.error(e.getMessage());
		for (StackTraceElement se : e.getStackTrace()) {
			xTriggerLog.error(se.toString());
		}
	}

	@Extension
	public static class OntrackValidationStampPollingTriggerDescriptor extends XTriggerDescriptor {
		@Override
		public boolean isApplicable(Item item) {
			return true;
		}

		@Override
		public String getDisplayName() {
			return "Ontrack: Poll promotion level";
		}
	}
}
