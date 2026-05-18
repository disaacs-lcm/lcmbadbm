package edu.touro.mco152.bm;

/**
 * Any background task that runs {@link DiskBenchmarker}.
 * It uses this interface to report progress, publish results, and check for
 * cancellation without depending on SwingWorker.
 */
public interface DiskBenchmarkCaller {

	/**
	 * Returns true if the user has requested to cancel the benchmark.
	 */
	boolean isCancelled();

	/**
	 * Reports how far along the benchmark is.
	 *
	 * @param progress a value from 0 to 100
	 */
	void setBenchmarkProgress(int progress);

	/**
	 * Sends completed benchmark results to the UI thread for display.
	 *
	 * @param chunks one or more DiskMark results to publish
	 */
	void publishChunks(DiskMark... chunks);
}
