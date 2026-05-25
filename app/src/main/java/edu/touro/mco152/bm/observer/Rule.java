package edu.touro.mco152.bm.observer;

import edu.touro.mco152.bm.persist.DiskRun;

/**
 * A rule to be called by {@link RulesObserver}, if the condition is met.
 */
public interface Rule {

	/**
	 * Check if the condition is met.
	 * 
	 * @param run
	 *            Results of the benchmark to check.
	 * @return
	 *         If the commission is met.
	 */
	boolean conditionMet(DiskRun run);

	/**
	 * Perform a function if the condition was met.
	 * 
	 * @param run
	 *            Results of the benchmark
	 */
	void execute(DiskRun run);
}
