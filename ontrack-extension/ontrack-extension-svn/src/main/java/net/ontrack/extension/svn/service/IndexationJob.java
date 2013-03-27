package net.ontrack.extension.svn.service;

public interface IndexationJob {

	boolean isRunning();

	long getMin();

	long getMax();

	long getCurrent();

	/**
	 * Returns the job progression in percentage (0..100)
	 */
	int getProgress();
}
