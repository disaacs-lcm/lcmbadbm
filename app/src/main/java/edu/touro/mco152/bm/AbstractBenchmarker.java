package edu.touro.mco152.bm;

import java.util.Date;

import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.persist.EM;
import jakarta.persistence.EntityManager;

/**
 * Base class for disk benchmark operations.
 * Contains shared setup and utility logic used by both read and write benchmarks.
 */
public abstract class AbstractBenchmarker {

	/**
	 * Allocates and fills a block buffer with alternating 0xFF/0x00 bytes.
	 * The size is determined by the current block size setting in {@link App}.
	 *
	 * @return the initialized byte array to use as read/write data
	 */
	protected byte[] initBlockBuffer() {
		int blockSize = App.blockSizeKb * App.KILOBYTE;
		byte[] blockArr = new byte[blockSize];
		for (int b = 0; b < blockArr.length; b++) {
			if (b % 2 == 0) {
				blockArr[b] = (byte) 0xFF;
			}
		}
		return blockArr;
	}

	/**
	 * Computes the total number of benchmark units for write, read, and combined.
	 * A "unit" is one block in one mark. Used to calculate overall progress percentage.
	 *
	 * @return int array of [wUnitsTotal, rUnitsTotal, unitsTotal]
	 */
	protected int[] computeUnitTotals() {
		int wUnitsTotal = App.writeTest ? App.numOfBlocks * App.numOfMarks : 0;
		int rUnitsTotal = App.readTest ? App.numOfBlocks * App.numOfMarks : 0;
		return new int[]{wUnitsTotal, rUnitsTotal, wUnitsTotal + rUnitsTotal};
	}

	/**
	 * Creates and configures a {@link DiskRun} for the given IO mode.
	 * Populates run metadata from {@link App} settings, logs disk info, and initializes the UI legend.
	 *
	 * @param mode the IO direction (READ or WRITE)
	 * @param ui   the UI to update with disk info
	 * @return the configured DiskRun ready to collect benchmark results
	 */
	protected DiskRun initRun(DiskRun.IOMode mode, DiskUI ui) {
		DiskRun run = new DiskRun(mode, App.blockSequence);
		run.setNumMarks(App.numOfMarks);
		run.setNumBlocks(App.numOfBlocks);
		run.setBlockSize(App.blockSizeKb);
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

	/**
	 * Persists the completed run to the database and sends it to the UI for display.
	 *
	 * @param run the completed benchmark run to save
	 * @param ui  the UI panel to add the run to
	 */
	protected void persistRun(DiskRun run, DiskUI ui) {
		EntityManager em = EM.getEntityManager();
		em.getTransaction().begin();
		em.persist(run);
		em.getTransaction().commit();
		ui.addRun(run);
	}
}
