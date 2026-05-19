package edu.touro.mco152.bm;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.persist.EM;
import jakarta.persistence.EntityManager;

public class WriteBenchmarker {
	public void run(DiskBenchmarkCaller caller, DiskUI ui) {

		/*
		 * init local vars that keep track of benchmarks, and a large read/write buffer
		 */
		int wUnitsComplete = 0, rUnitsComplete = 0, unitsComplete;
		int wUnitsTotal = App.writeTest ? App.numOfBlocks * App.numOfMarks : 0;
		int rUnitsTotal = App.readTest ? App.numOfBlocks * App.numOfMarks : 0;
		int unitsTotal = wUnitsTotal + rUnitsTotal;
		float percentComplete;

		int blockSize = App.blockSizeKb * App.KILOBYTE;
		byte[] blockArr = new byte[blockSize];
		for (int b = 0; b < blockArr.length; b++) {
			if (b % 2 == 0) {
				blockArr[b] = (byte) 0xFF;
			}
		}

		DiskMark wMark; // declare vars that will point to objects used to pass progress to UI

		int startFileNum = App.nextMarkNumber;
		DiskRun run = new DiskRun(DiskRun.IOMode.WRITE, App.blockSequence);
		run.setNumMarks(App.numOfMarks);
		run.setNumBlocks(App.numOfBlocks);
		run.setBlockSize(App.blockSizeKb);
		run.setTxSize(App.targetTxSizeKb());
		run.setDiskInfo(Util.getDiskInfo(App.dataDir));

		// Tell logger and UI to display what we know so far about the Run
		App.msg("disk info: (" + run.getDiskInfo() + ")");

		ui.initLegend(run.getDiskInfo());

		// Create a test data file using the default file system and config-specified
		// location
		if (!App.multiFile) {
			App.testFile = new File(App.dataDir.getAbsolutePath() + File.separator + "testdata.jdm");
		}

		/*
		 * Begin an outer loop for specified duration (number of 'marks') of benchmark,
		 * that keeps writing data (in its own loop - for specified # of blocks). Each
		 * 'Mark' is timed
		 * and is reported to the UI for display as each Mark completes.
		 */
		for (int m = startFileNum; m < startFileNum + App.numOfMarks && !caller.isCancelled(); m++) {

			if (App.multiFile) {
				App.testFile = new File(App.dataDir.getAbsolutePath()
						+ File.separator + "testdata" + m + ".jdm");
			}
			wMark = new DiskMark(DiskMark.MarkType.WRITE); // starting to keep track of a new benchmark
			wMark.setMarkNum(m);
			long startTime = System.nanoTime();
			long totalBytesWrittenInMark = 0;

			String mode = "rw";
			if (App.writeSyncEnable) {
				mode = "rwd";
			}

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

						/*
						 * Report to UI what percentage level of Entire BM (#Marks * #Blocks) is
						 * done.
						 */
						caller.setBenchmarkProgress((int) percentComplete);
					}
				}
			} catch (IOException ex) {
				Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
			}

			/*
			 * Compute duration, throughput of this Mark's step of BM
			 */
			long endTime = System.nanoTime();
			long elapsedTimeNs = endTime - startTime;
			double sec = (double) elapsedTimeNs / (double) 1000000000;
			double mbWritten = (double) totalBytesWrittenInMark / (double) App.MEGABYTE;
			wMark.setBwMbSec(mbWritten / sec);
			App.msg("m:" + m + " write IO is " + wMark.getBwMbSecAsString() + " MB/s     "
					+ "(" + Util.displayString(mbWritten) + "MB written in "
					+ Util.displayString(sec) + " sec)");
			App.updateMetrics(wMark);

			/*
			 * Let the GUI know the interim result described by the current Mark
			 */
			caller.publishChunks(wMark);

			// Keep track of statistics to be displayed and persisted after all Marks are
			// done.
			run.setRunMax(wMark.getCumMax());
			run.setRunMin(wMark.getCumMin());
			run.setRunAvg(wMark.getCumAvg());
			run.setEndTime(new Date());
		} // END outer loop for specified duration (number of 'marks') for WRITE benchmark

		/*
		 * Persist info about the Write BM Run (e.g. into Derby Database) and add it to
		 * a GUI panel
		 */
		EntityManager em = EM.getEntityManager();
		em.getTransaction().begin();
		em.persist(run);
		em.getTransaction().commit();

		ui.addRun(run);
	}
}
