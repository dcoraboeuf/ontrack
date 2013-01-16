package net.ontrack.core.ui;

import net.ontrack.core.model.BuildDefinitionForm;
import net.ontrack.core.model.ID;
import net.ontrack.core.model.ValidationRunDefinitionForm;

/**
 * This UI is used by clients that will run builds on existing project branches.
 */
public interface ControlUI {

	/**
	 * Defines a build
	 */
	ID createBuild(String project, String branch, BuildDefinitionForm build);

	/**
	 * Defines a validation run
	 */
	ID createValidationRun(String project, String branch, String build, String validationStamp, ValidationRunDefinitionForm validationRun);

}
