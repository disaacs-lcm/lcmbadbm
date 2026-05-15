package edu.touro.mco152.bm;

import edu.touro.mco152.bm.persist.DiskRun;

public interface DiskUI {

	public void initLegend(String titleText);

	public void updateLegend();

	public void resetTestData();

	public void addReadMark(DiskMark mark);

	public void addWriteMark(DiskMark mark);

	public void addRun(DiskRun run);

	public void sendPlainMessage(String msg, String title);

	public void sendErrorMessage(String msg, String title);

	public void adjustSensitivity();
}
