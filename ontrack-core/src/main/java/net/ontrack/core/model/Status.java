package net.ontrack.core.model;

/**
 * List of available statuses for the validation runs.
 *
 * Adding or removing some statuses implies to change as well:
 * <ul>
 *     <li><i>general.css</i></li>
 *     <li>the <i>status-*.png</i> images</li>
 * </ul>
 */
public enum Status {

    /**
     * Run passed
     */
    PASSED,
    /**
     * Run was interrupted
     */
    INTERRUPTED,
    /**
     * Run has failed
     */
    FAILED,
    /**
     * Run is under investigation
     */
    INVESTIGATED,
    /**
     * Run should now be fixed
     */
    FIXED,
    /**
     * Run has shown a defect
     */
    DEFECTIVE;

}
