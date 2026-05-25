package edu.touro.mco152.bm.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.touro.mco152.bm.*;
import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.persist.DiskRun.BlockSequence;

/**
 * Base class for disk benchmark operations.
 * Contains shared setup and utility logic used by both read and write benchmarks.
 */
public abstract class AbstractBenchmarker {

	protected DiskUI ui;
	protected DiskBenchmarkCaller caller;
	protected int numOfMarks, numOfBlocks, blockSizeKb;
	protected BlockSequence blockSequence;

	/**
	 * Allocates and fills a block buffer with alternating 0xFF/0x00 bytes.
	 * The size is determined by the current block size setting in {@link App}.
	 *
	 * @return the initialized byte array to use as read/write data
	 */
	protected byte[] initBlockBuffer() {
		int blockSize = blockSizeKb * App.KILOBYTE;
		byte[] blockArr = new byte[blockSize];
		for (int b = 0; b < blockArr.length; b++) {
			if (b % 2 == 0) {
				blockArr[b] = (byte) 0xFF;
			}
		}
		return blockArr;
	}

	/**
	 * Creates and configures a {@link DiskRun} for the given IO mode.
	 * Populates run metadata from {@link App} settings, logs disk info, and initializes the UI legend.
	 *
	 * @param mode the IO direction (READ or WRITE)
	 * @return the configured DiskRun ready to collect benchmark results
	 */
	protected DiskRun initRun(DiskRun.IOMode mode) {
		DiskRun run = new DiskRun(mode, blockSequence);
		run.setNumMarks(numOfMarks);
		run.setNumBlocks(numOfBlocks);
		run.setBlockSize(blockSizeKb);
		run.setTxSize(App.targetTxSizeKb());
		run.setDiskInfo(Util.getDiskInfo(App.dataDir));
		App.msg("disk info: (" + run.getDiskInfo() + ")");
		ui.initLegend(run.getDiskInfo());
		return run;
	}

	/**
	 * Updates the run's cumulative statistics from the latest completed mark.
	 * Called after each mark finishes to keep max, min, avg, and end time current.
	 *
	 * @param run  the run whose statistics are being updated
	 * @param mark the most recently completed mark
	 */
	protected void updateRunStats(DiskRun run, DiskMark mark) {
		run.setRunMax(mark.getCumMax());
		run.setRunMin(mark.getCumMin());
		run.setRunAvg(mark.getCumAvg());
		run.setEndTime(new Date());
	}

	protected List<BenchmarkObserver> observers = new ArrayList<>();

	public void registerObserver(BenchmarkObserver o) {
		observers.add(o);
	}
}
