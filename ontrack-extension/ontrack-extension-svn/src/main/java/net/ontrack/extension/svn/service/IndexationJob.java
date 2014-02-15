package net.ontrack.extension.svn.service;

import net.ontrack.extension.svn.service.model.SVNRepository;

public interface IndexationJob {

	boolean isRunning();

	long getMin();

	long getMax();

	long getCurrent();

	/**
	 * Returns the job progression in percentage (0..100)
	 */
	int getProgress();

    /**
     * Returns the associated SVN repository
     */
    SVNRepository getRepository();
}
