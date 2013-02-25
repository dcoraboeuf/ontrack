package net.ontrack.service;

import net.ontrack.core.model.*;

public interface ControlService {

	BuildSummary createBuild(int branch, BuildCreationForm form);

	ValidationRunSummary createValidationRun(int build, int validationStamp, ValidationRunCreationForm validationRun);

    PromotedRunSummary createPromotedRun(int buildId, int promotionLevel, PromotedRunCreationForm promotedRun);
}
