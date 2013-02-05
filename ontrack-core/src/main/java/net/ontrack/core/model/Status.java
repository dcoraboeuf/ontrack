package net.ontrack.core.model;

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
     * Run has shown a defect
     */
    DEFECTIVE;

}
