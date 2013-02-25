package net.ontrack.core.ui;

import net.ontrack.core.model.*;

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

    /**
     * Promotes a build
     */
    PromotedRunSummary createPromotedRun(String project, String branch, String build, String promotionLevel, PromotedRunCreationForm form);

}
