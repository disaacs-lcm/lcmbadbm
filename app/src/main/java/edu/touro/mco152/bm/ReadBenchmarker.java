package edu.touro.mco152.bm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.touro.mco152.bm.persist.DiskRun;

/**
 * Runs the read portion of the disk benchmark.
 * Reads blocks from an existing test file and measures throughput for each mark.
 */
public class ReadBenchmarker extends AbstractBenchmarker {

	/**
	 * Executes the read benchmark across all configured marks and blocks.
	 * Reads data from the test file, tracks throughput per mark, and reports progress to the UI.
	 *
	 * @param caller provides cancellation checks and progress reporting
	 * @param ui     the UI to update as the benchmark runs
	 * @return {@code true} if the benchmark completed successfully, {@code false} if the test file was not found
	 * @throws Exception if an unexpected IO error occurs
	 */
	public Boolean runBenchmark(DiskBenchmarkCaller caller, DiskUI ui) throws Exception {
		int[] unitTotals = computeUnitTotals();
		int wUnitsTotal = unitTotals[0], rUnitsTotal = unitTotals[1], unitsTotal = unitTotals[2];
		int wUnitsComplete = 0, rUnitsComplete = 0, unitsComplete;
		float percentComplete;

		int blockSize = App.blockSizeKb * App.KILOBYTE;
		byte[] blockArr = initBlockBuffer();

		DiskMark rMark;

		int startFileNum = App.nextMarkNumber;
		DiskRun run = initRun(DiskRun.IOMode.READ, ui);

		for (int m = startFileNum; m < startFileNum + App.numOfMarks && !caller.isCancelled(); m++) {

			if (App.multiFile) {
				App.testFile = new File(App.dataDir.getAbsolutePath()
						+ File.separator + "testdata" + m + ".jdm");
			}
			rMark = new DiskMark(DiskMark.MarkType.READ);
			rMark.setMarkNum(m);
			long startTime = System.nanoTime();
			long totalBytesReadInMark = 0;

			try {
				try (RandomAccessFile rAccFile = new RandomAccessFile(App.testFile, "r")) {
					for (int b = 0; b < App.numOfBlocks; b++) {
						if (App.blockSequence == DiskRun.BlockSequence.RANDOM) {
							int rLoc = Util.randInt(0, App.numOfBlocks - 1);
							rAccFile.seek((long) rLoc * blockSize);
						} else {
							rAccFile.seek((long) b * blockSize);
						}
						rAccFile.readFully(blockArr, 0, blockSize);
						totalBytesReadInMark += blockSize;
						rUnitsComplete++;
						unitsComplete = rUnitsComplete + wUnitsComplete;
						percentComplete = (float) unitsComplete / (float) unitsTotal * 100f;
						caller.setBenchmarkProgress((int) percentComplete);
					}
				}
			} catch (FileNotFoundException ex) {
				Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
				String emsg = "May not have done Write Benchmarks, so no data available to read." +
						ex.getMessage();
				App.msg(emsg);
				return false;
			}
			long endTime = System.nanoTime();
			long elapsedTimeNs = endTime - startTime;
			double sec = (double) elapsedTimeNs / (double) 1000000000;
			double mbRead = (double) totalBytesReadInMark / (double) App.MEGABYTE;
			rMark.setBwMbSec(mbRead / sec);
			App.msg("m:" + m + " READ IO is " + rMark.getBwMbSec() + " MB/s    "
					+ "(MBread " + mbRead + " in " + sec + " sec)");
			App.updateMetrics(rMark);
			caller.publishChunks(rMark);

			updateRunStats(run, rMark);
		}

		persistRun(run, ui);
		return true;
	}
}
