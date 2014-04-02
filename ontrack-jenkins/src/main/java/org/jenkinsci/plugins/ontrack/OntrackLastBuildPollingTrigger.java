package org.jenkinsci.plugins.ontrack;

import antlr.ANTLRException;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Item;
import hudson.model.Node;
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

import java.io.File;
import java.io.IOException;

public class OntrackLastBuildPollingTrigger extends AbstractTrigger {
	private final String project;
	private final String branch;

	@DataBoundConstructor
	public OntrackLastBuildPollingTrigger(String cronTabSpec, String triggerLabel, String project, String branch) throws ANTLRException {
		super(cronTabSpec, triggerLabel);
		this.project = project;
		this.branch = branch;
    }

	@Override
	protected File getLogFile() {
		return new File(job.getRootDir(), "ontrack-lastbuild-polling-trigger.log");
	}

	@Override
	protected boolean requiresWorkspaceForPolling() {
		return false;
	}

	@Override
	protected String getName() {
		return "Ontrack: Last build";
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

		FilePath lastBuildNrFile = new FilePath(node.getRootPath(), String.format("%s-lastbuild-lastBuildNr", job.getName()));
		String lastBuildNr = loadLastBuildNr(xTriggerLog, lastBuildNrFile);

		// Gets the last build
        OptionalBuildSummary optionLastBuild = getBuildSummary(actualBranch, actualProject);

		// Found
		if (optionLastBuild.getBuild() != null) {
            BuildSummary lastBuild = optionLastBuild.getBuild();
            String name = lastBuild.getName();
			xTriggerLog.info(String.format("Found build '%s' for branch '%s' and project '%s'%n", name, actualBranch, actualProject));
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
		return String.format("New build found");
	}

	public String getProject() {
		return project;
	}

	public String getBranch() {
		return branch;
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
		return false;
	}

	private OptionalBuildSummary getBuildSummary(final String branch, final String project) {
		return OntrackClient.manage(new ManageClientCall<OptionalBuildSummary>() {
			@Override
			public OptionalBuildSummary onCall(ManageUIClient ui) {
				return ui.getLastBuild(project, branch);
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
			return "Ontrack: Poll last build";
		}
	}
}
