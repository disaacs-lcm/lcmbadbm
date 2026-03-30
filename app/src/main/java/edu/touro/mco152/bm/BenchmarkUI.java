package edu.touro.mco152.bm;

import edu.touro.mco152.bm.persist.DiskRun;

public interface BenchmarkUI {
    void updateLegend();
    void resetTestData();
    void setChartTitle(String title);
    void addRun(DiskRun run);
    void addWriteMark(DiskMark dm);
    void addReadMark(DiskMark dm);
    void adjustSensitivity();
    void showMessage(String message, String title);
    void updateProgress(int value, String progressStr);
    void msg(String message);
}
