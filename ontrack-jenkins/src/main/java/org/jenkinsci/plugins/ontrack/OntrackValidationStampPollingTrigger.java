package org.jenkinsci.plugins.ontrack;

import antlr.ANTLRException;
import hudson.Extension;
import hudson.model.Action;
import hudson.model.Item;
import hudson.model.Node;
import net.ontrack.client.ManageUIClient;
import net.ontrack.core.model.BuildSummary;
import org.jenkinsci.lib.xtrigger.AbstractTrigger;
import org.jenkinsci.lib.xtrigger.XTriggerDescriptor;
import org.jenkinsci.lib.xtrigger.XTriggerException;
import org.jenkinsci.lib.xtrigger.XTriggerLog;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;
import java.util.logging.Logger;

public class OntrackValidationStampPollingTrigger extends AbstractTrigger {
	private final String project;
	private final String branch;
	private final String validationStamp;
	private String lastBuildNr;

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
		if (project == null || project.isEmpty()) {
			xTriggerLog.info("Ontrack: No project configured");
			return false;
		}

		if (branch == null || branch.isEmpty()) {
			xTriggerLog.info("Ontrack: No branch configured");
			return false;
		}

		if (validationStamp == null || validationStamp.isEmpty()) {
			xTriggerLog.info("Ontrack: No validation stamp configured");
			return false;
		}

		// Gets the last build
		BuildSummary lastBuild = OntrackClient.manage(new ManageClientCall<BuildSummary>() {
			@Override
			public BuildSummary onCall(ManageUIClient ui) {
				return ui.getLastBuildWithValidationStamp(null, project, branch, validationStamp);
			}
		});

		// Found
		if (lastBuild != null) {
			String name = lastBuild.getName();
			xTriggerLog.info(String.format("Found build '%s' for branch '%s' and project '%s' and validation stamp '%s'%n", name, branch, project, validationStamp));
			if (lastBuildNr == null || lastBuildNr.isEmpty() || !lastBuildNr.equals(name)) {
				lastBuildNr = name;
				return true;
			}
		}

		return false;
	}

	@Override
	protected String getCause() {
		return "Some cause";
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

	public String getLastBuildNr() {
		return lastBuildNr;
	}

	public void setLastBuildNr(String lastBuildNr) {
		this.lastBuildNr = lastBuildNr;
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
