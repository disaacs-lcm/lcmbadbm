package edu.touro.mco152.bm;

import javax.swing.SwingWorker;
import java.util.List;
import java.util.logging.Logger;

public class SwingDiskWorker extends SwingWorker<Boolean, DiskMark>
        implements BenchmarkContext {

    private final BenchmarkUI ui;
    private final DiskWorker diskWorker;

    public SwingDiskWorker(BenchmarkUI ui) {
        this.ui = ui;
        this.diskWorker = new DiskWorker(ui, this);
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        return diskWorker.runBenchmark();
    }

    @Override
    protected void process(List<DiskMark> markList) {
        diskWorker.processMarks(markList);
    }

    @Override
    protected void done() {
        Boolean status = null;
        try {
            status = get();
        } catch (Exception e) {
            Logger.getLogger(App.class.getName())
                  .warning("Problem obtaining final status: " + e.getMessage());
        }
        diskWorker.onComplete(status);
    }

    // BenchmarkContext implementation
    @Override
    public void reportProgress(int pct) {
        super.setProgress(pct);
        long kbProcessed = (long) pct * App.targetTxSizeKb() / 100;
        ui.updateProgress(pct, kbProcessed + " / " + App.targetTxSizeKb());
    }

    @Override
    public void publishMark(DiskMark mark) { publish(mark); }

    @Override
    public boolean checkCancelled() { return super.isCancelled(); }

    public Boolean getLastStatus() { return diskWorker.getLastStatus(); }
}
