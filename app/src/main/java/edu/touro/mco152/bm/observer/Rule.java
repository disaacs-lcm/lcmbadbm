package edu.touro.mco152.bm.observer;

import edu.touro.mco152.bm.persist.DiskRun;

public interface Rule {

	boolean conditionMet(DiskRun run);

	void execute(DiskRun run);
}
