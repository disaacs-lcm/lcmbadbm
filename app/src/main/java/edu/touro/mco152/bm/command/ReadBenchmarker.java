package edu.touro.mco152.bm.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.touro.mco152.bm.*;
import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.persist.DiskRun.BlockSequence;

/**
 * Runs the read portion of the disk benchmark.
 * Reads blocks from an existing test file and measures throughput for each mark.
 */
public class ReadBenchmarker extends AbstractBenchmarker implements BenchmarkCommand {

	public ReadBenchmarker(DiskUI ui, DiskBenchmarkCaller caller, int numOfMarks, int numOfBlocks, int blockSizeKb,
			BlockSequence blockSequence) {
		this.ui = ui;
		this.caller = caller;
		this.numOfMarks = numOfMarks;
		this.numOfBlocks = numOfBlocks;
		this.blockSizeKb = blockSizeKb;
		this.blockSequence = blockSequence;
	}

	private List<BenchmarkObserver> observers = new ArrayList<>();

	public void registerObserver(BenchmarkObserver o) {
		observers.add(o);
	}

	/**
	 * Executes the read benchmark across all configured marks and blocks.
	 * Reads data from the test file, tracks throughput per mark, and reports progress to the UI.
	 */
	public void run() {
		int wUnitsTotal = 0;
		int rUnitsTotal = numOfBlocks * numOfMarks;
		int unitsTotal = wUnitsTotal + rUnitsTotal;
		int wUnitsComplete = 0, rUnitsComplete = 0, unitsComplete;
		float percentComplete;

		int blockSize = blockSizeKb * App.KILOBYTE;
		byte[] blockArr = initBlockBuffer();

		DiskMark rMark;

		int startFileNum = App.nextMarkNumber;
		DiskRun run = initRun(DiskRun.IOMode.READ);

		for (int m = startFileNum; m < startFileNum + numOfMarks && !caller.isCancelled(); m++) {

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
					for (int b = 0; b < numOfBlocks; b++) {
						if (blockSequence == DiskRun.BlockSequence.RANDOM) {
							int rLoc = Util.randInt(0, numOfBlocks - 1);
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
			} catch (IOException ioe) {
				Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ioe);
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

		for (BenchmarkObserver o : observers) {
			o.update(run);
		}
	}
}
