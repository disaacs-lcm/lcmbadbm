package edu.touro.mco152.bm.observer;

import edu.touro.mco152.bm.Util;
import edu.touro.mco152.bm.externalsys.SlackManager;
import edu.touro.mco152.bm.persist.DiskRun;

/**
 * Sends a message on Slack if a read benchmark is 3% above average.
 * To be called by {@link RulesObserver}
 */
public class SlackRule implements Rule {
	private final SlackManager sm = new SlackManager("BadBM");

	@Override
	public boolean conditionMet(DiskRun run) {
		return run.getIoMode() == DiskRun.IOMode.READ
				&& run.getRunMax() > run.getRunAvg() * 1.03;
	}

	@Override
	public void execute(DiskRun run) {
		sm.postMsg2OurChannel(
				"Read benchmark iteration (" + Util.displayString(run.getRunMax()) +
						" MB/s) is >3% above average ("
						+ Util.displayString(run.getRunAvg()) + " MB/s)!");
	}
}
