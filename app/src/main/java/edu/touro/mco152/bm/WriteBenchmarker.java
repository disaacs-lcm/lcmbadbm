package edu.touro.mco152.bm;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.touro.mco152.bm.persist.DiskRun;

/**
 * Runs the write portion of the disk benchmark.
 * Writes blocks to a test file and measures throughput for each mark.
 */
public class WriteBenchmarker extends AbstractBenchmarker {

	/**
	 * Executes the write benchmark across all configured marks and blocks.
	 * Writes data to the test file, tracks throughput per mark, and reports progress to the UI.
	 *
	 * @param caller provides cancellation checks and progress reporting
	 * @param ui     the UI to update as the benchmark runs
	 */
	public void run(DiskBenchmarkCaller caller, DiskUI ui) {
		int[] unitTotals = computeUnitTotals();
		int wUnitsTotal = unitTotals[0], rUnitsTotal = unitTotals[1], unitsTotal = unitTotals[2];
		int wUnitsComplete = 0, rUnitsComplete = 0, unitsComplete;
		float percentComplete;

		int blockSize = App.blockSizeKb * App.KILOBYTE;
		byte[] blockArr = initBlockBuffer();

		DiskMark wMark;

		int startFileNum = App.nextMarkNumber;
		DiskRun run = initRun(DiskRun.IOMode.WRITE, ui);

		if (!App.multiFile) {
			App.testFile = new File(App.dataDir.getAbsolutePath() + File.separator + "testdata.jdm");
		}

		for (int m = startFileNum; m < startFileNum + App.numOfMarks && !caller.isCancelled(); m++) {

			if (App.multiFile) {
				App.testFile = new File(App.dataDir.getAbsolutePath()
						+ File.separator + "testdata" + m + ".jdm");
			}
			wMark = new DiskMark(DiskMark.MarkType.WRITE);
			wMark.setMarkNum(m);
			long startTime = System.nanoTime();
			long totalBytesWrittenInMark = 0;

			String mode = App.writeSyncEnable ? "rwd" : "rw";

			try {
				try (RandomAccessFile rAccFile = new RandomAccessFile(App.testFile, mode)) {
					for (int b = 0; b < App.numOfBlocks; b++) {
						if (App.blockSequence == DiskRun.BlockSequence.RANDOM) {
							int rLoc = Util.randInt(0, App.numOfBlocks - 1);
							rAccFile.seek((long) rLoc * blockSize);
						} else {
							rAccFile.seek((long) b * blockSize);
						}
						rAccFile.write(blockArr, 0, blockSize);
						totalBytesWrittenInMark += blockSize;
						wUnitsComplete++;
						unitsComplete = rUnitsComplete + wUnitsComplete;
						percentComplete = (float) unitsComplete / (float) unitsTotal * 100f;
						caller.setBenchmarkProgress((int) percentComplete);
					}
				}
			} catch (IOException ex) {
				Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
			}

			long endTime = System.nanoTime();
			long elapsedTimeNs = endTime - startTime;
			double sec = (double) elapsedTimeNs / (double) 1000000000;
			double mbWritten = (double) totalBytesWrittenInMark / (double) App.MEGABYTE;
			wMark.setBwMbSec(mbWritten / sec);
			App.msg("m:" + m + " write IO is " + wMark.getBwMbSecAsString() + " MB/s     "
					+ "(" + Util.displayString(mbWritten) + "MB written in "
					+ Util.displayString(sec) + " sec)");
			App.updateMetrics(wMark);
			caller.publishChunks(wMark);

			updateRunStats(run, wMark);
		}

		persistRun(run, ui);
	}
}
