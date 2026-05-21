package edu.touro.mco152.bm.command;

import edu.touro.mco152.bm.BenchmarkObserver;
import edu.touro.mco152.bm.persist.DiskRun;

public class mockObserver implements BenchmarkObserver {

	private static boolean notified = false;

	public static boolean wasNotified() {
		return notified;
	}

	@Override
	public void update(DiskRun run) {
		notified = true;
	}

}
