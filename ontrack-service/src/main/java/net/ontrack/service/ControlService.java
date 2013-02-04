package net.ontrack.service;

import net.ontrack.core.model.BuildCreationForm;
import net.ontrack.core.model.BuildSummary;
import net.ontrack.core.model.ValidationRunCreationForm;
import net.ontrack.core.model.ValidationRunSummary;

public interface ControlService {

	BuildSummary createBuild(int branch, BuildCreationForm form);

	ValidationRunSummary createValidationRun(int build, int validationStamp, ValidationRunCreationForm validationRun);

}
