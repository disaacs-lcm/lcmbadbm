package edu.touro.mco152.bm;

import java.util.List;
import java.util.logging.Logger;

import javax.swing.SwingWorker;

public class SwingDiskWorker extends SwingWorker<Boolean, DiskMark> implements DiskBenchmarkCaller {

	// Record any success or failure status returned from SwingWorker (might be us
	// or super)
	Boolean lastStatus = null; // so far unknown

	private DiskUI ui = null;

	public SwingDiskWorker(DiskUI ui) {
		this.ui = ui;
	}

	@Override
	protected Boolean doInBackground() throws Exception {
		return new DiskBenchmarker(ui, this).runBenchmark();
	}

	@Override
	public void publishChunks(DiskMark... chunks) {
		publish(chunks);
	}

	@Override
	public void setBenchmarkProgress(int progress) {
		setProgress(progress);
	}

	protected void done() {
		// Obtain final status, might from doInBackground ret value, or SwingWorker
		// error
		try {
			lastStatus = super.get(); // record for future access
		} catch (Exception e) {
			Logger.getLogger(App.class.getName())
					.warning("Problem obtaining final status: " + e.getMessage());
		}

		if (App.autoRemoveData) {
			Util.deleteDirectory(App.dataDir);
		}
		App.state = App.State.IDLE_STATE;
		ui.adjustSensitivity();
	}

	/**
	 * Process a list of 'chunks' that have been processed, ie that our thread has
	 * previously
	 * published to Swing. For my info, watch Professor Cohen's video -
	 * Module_6_RefactorBadBM Swing_DiskWorker_Tutorial.mp4
	 * 
	 * @param markList a list of DiskMark objects reflecting some completed
	 *                 benchmarks
	 */
	@Override
	protected void process(List<DiskMark> markList) {
		markList.stream().forEach((dm) -> {
			if (dm.type == DiskMark.MarkType.WRITE) {
				ui.addWriteMark(dm);
			} else {
				ui.addReadMark(dm);
			}
		});
	}

	public Boolean getLastStatus() {
		return lastStatus;
	}
}
