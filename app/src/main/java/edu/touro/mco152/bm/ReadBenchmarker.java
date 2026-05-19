package edu.touro.mco152.bm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.persist.EM;
import jakarta.persistence.EntityManager;

public class ReadBenchmarker {

	public Boolean runBenchmark(DiskBenchmarkCaller caller, DiskUI ui) throws Exception {
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

		DiskMark rMark; // declare vars that will point to objects used to pass progress to UI

		int startFileNum = App.nextMarkNumber;
		DiskRun run = new DiskRun(DiskRun.IOMode.READ, App.blockSequence);
		run.setNumMarks(App.numOfMarks);
		run.setNumBlocks(App.numOfBlocks);
		run.setBlockSize(App.blockSizeKb);
		run.setTxSize(App.targetTxSizeKb());
		run.setDiskInfo(Util.getDiskInfo(App.dataDir));

		App.msg("disk info: (" + run.getDiskInfo() + ")");

		ui.initLegend(run.getDiskInfo());

		for (int m = startFileNum; m < startFileNum + App.numOfMarks && !caller.isCancelled(); m++) {

			if (App.multiFile) {
				App.testFile = new File(App.dataDir.getAbsolutePath()
						+ File.separator + "testdata" + m + ".jdm");
			}
			rMark = new DiskMark(DiskMark.MarkType.READ); // starting to keep track of a new benchmark
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

			run.setRunMax(rMark.getCumMax());
			run.setRunMin(rMark.getCumMin());
			run.setRunAvg(rMark.getCumAvg());
			run.setEndTime(new Date());
		}

		/*
		 * Persist info about the Read BM Run (e.g. into Derby Database) and add it to a
		 * GUI panel
		 */
		EntityManager em = EM.getEntityManager();
		em.getTransaction().begin();
		em.persist(run);
		em.getTransaction().commit();

		ui.addRun(run);

		return true;
	}
}
