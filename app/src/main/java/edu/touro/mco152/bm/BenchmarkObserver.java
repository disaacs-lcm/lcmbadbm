package edu.touro.mco152.bm;

import edu.touro.mco152.bm.persist.DiskRun;

/**
 * This interface is part of the observer pattern.
 * Observers that implement this interface are eligible to be registered by
 * {@link DiskBenchmarker} to be notified when a benchmark ends.
 */
public interface BenchmarkObserver {

	/**
	 * The method called by the benchmark command (subject).
	 * Uses the strategy pattern.
	 * 
	 * @param run
	 *            The result of the benchmark.
	 */
	public void update(DiskRun run);

}
