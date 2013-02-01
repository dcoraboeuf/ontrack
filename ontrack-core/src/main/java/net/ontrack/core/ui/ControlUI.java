package net.ontrack.core.ui;

import net.ontrack.core.model.BuildCreationForm;
import net.ontrack.core.model.BuildSummary;
import net.ontrack.core.model.ValidationRunCreationForm;
import net.ontrack.core.model.ValidationRunSummary;

/**
 * This UI is used by clients that will run builds on existing project branches.
 */
public interface ControlUI {

	/**
	 * Defines a build
	 */
	BuildSummary createBuild(String project, String branch, BuildCreationForm build);

	/**
	 * Defines a validation run
	 */
	ValidationRunSummary createValidationRun(String project, String branch, String build, String validationStamp, ValidationRunCreationForm validationRun);

}
