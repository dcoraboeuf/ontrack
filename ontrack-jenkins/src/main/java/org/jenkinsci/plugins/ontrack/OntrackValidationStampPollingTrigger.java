package org.jenkinsci.plugins.ontrack;

import antlr.ANTLRException;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.*;
import net.ontrack.client.ManageUIClient;
import net.ontrack.client.support.ManageClientCall;
import net.ontrack.core.model.BuildSummary;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.lib.xtrigger.AbstractTrigger;
import org.jenkinsci.lib.xtrigger.XTriggerDescriptor;
import org.jenkinsci.lib.xtrigger.XTriggerException;
import org.jenkinsci.lib.xtrigger.XTriggerLog;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.*;

public class OntrackValidationStampPollingTrigger extends AbstractTrigger {
	private final String project;
	private final String branch;
	private final String validationStamp;

	@DataBoundConstructor
	public OntrackValidationStampPollingTrigger(String cronTabSpec, String triggerLabel, String project, String branch,
												String validationStamp) throws ANTLRException {
		super(cronTabSpec, triggerLabel);
		this.project = project;
		this.branch = branch;
		this.validationStamp = validationStamp;
	}

	@Override
	protected File getLogFile() {
		return new File(job.getRootDir(), "ontrack-validationstamp-polling-trigger.log");
	}

	@Override
	protected boolean requiresWorkspaceForPolling() {
		return false;
	}

	@Override
	protected String getName() {
		return "Ontrack: Poll validation stamp";
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
        String actualValidationStamp = resolveEnvVars(validationStamp, (AbstractProject) job, node);

		FilePath lastBuildNrFile = new FilePath(node.getRootPath(), String.format("%s-lastBuildNr", job.getName()));
		String lastBuildNr = loadLastBuildNr(xTriggerLog, lastBuildNrFile);

		// Gets the last build
		BuildSummary lastBuild = getBuildSummary(actualProject, actualBranch, actualValidationStamp);

		// Found
		if (lastBuild != null) {
			String name = lastBuild.getName();
			xTriggerLog.info(String.format("Found build '%s' for branch '%s' and project '%s' and validation stamp '%s'%n", name, actualBranch, actualProject, actualValidationStamp));
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
		return String.format("New build found with validation stamp %s", validationStamp);
	}

	public String getProject() {
		return project;
	}

	public String getBranch() {
		return branch;
	}

	public String getValidationStamp() {
		return validationStamp;
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

		if (StringUtils.isEmpty(validationStamp)) {
			xTriggerLog.info("Ontrack: No validation stamp configured");
			return true;
		}
		return false;
	}

	private BuildSummary getBuildSummary(final String project, final String branch, final String validationStamp) {
		return OntrackClient.manage(new ManageClientCall<BuildSummary>() {
			@Override
			public BuildSummary onCall(ManageUIClient ui) {
				return ui.getLastBuildWithValidationStamp(null, project, branch, validationStamp).getBuild();
			}
		});
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
			return "Ontrack: Poll validation stamp";
		}
	}
}
