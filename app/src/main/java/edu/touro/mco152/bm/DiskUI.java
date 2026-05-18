package edu.touro.mco152.bm;

import edu.touro.mco152.bm.persist.DiskRun;

/**
 * Represents the UI that {@link DiskBenchmarker} can interact with.
 * Any class that implements this interface can be used as the UI for a
 * benchmark run.
 */
public interface DiskUI {

	/**
	 * Inits the chart and sets the title to the given text.
	 *
	 * @param titleText the title to display
	 */
	public void initLegend(String titleText);

	/** Refreshes the chart legend. */
	public void updateLegend();

	/** Clears all benchmark data shown in the UI. */
	public void resetTestData();

	/**
	 * Adds a read benchmark result to the chart.
	 *
	 * @param mark the read benchmark result
	 */
	public void addReadMark(DiskMark mark);

	/**
	 * Adds a write benchmark result to the chart.
	 *
	 * @param mark the write benchmark result
	 */
	public void addWriteMark(DiskMark mark);

	/**
	 * Adds a completed run to the run history panel.
	 *
	 * @param run the disk run to display
	 */
	public void addRun(DiskRun run);

	/**
	 * Shows a plain informational message dialog.
	 *
	 * @param msg   the message to display
	 * @param title the dialog title
	 */
	public void sendPlainMessage(String msg, String title);

	/**
	 * Shows an error message dialog.
	 *
	 * @param msg   the error message to display
	 * @param title the dialog title
	 */
	public void sendErrorMessage(String msg, String title);

	/** Enables or disables UI controls based on whether a benchmark is running. */
	public void adjustSensitivity();
}
