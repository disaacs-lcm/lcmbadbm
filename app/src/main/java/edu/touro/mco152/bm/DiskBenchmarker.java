package edu.touro.mco152.bm;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.touro.mco152.bm.command.*;
import static edu.touro.mco152.bm.App.*;

/**
 * Execute disk benchmarking as a Swing-compliant thread (only one of these threads can run at
 * once.) Cooperates with Swing to provide and make use of interim and final progress and
 * information, which is also recorded as needed to the persistence store, and log.
 * <p>
 * Depends on static values that describe the benchmark to be done having been set in App and ui classes.
 * The DiskRun class is used to keep track of and persist info about each benchmark at a higher level (a run),
 * while the DiskMark class described each iteration's result, which is displayed by the UI as the benchmark run
 * progresses.
 * <p>
 * This class only knows how to do 'read' or 'write' disk benchmarks. It is instantiated by the
 * startBenchmark() method.
 * <p>
 * To be Swing compliant this class extends SwingWorker and declares that its final return (when
 * doInBackground() is finished) is of type Boolean, and declares that intermediate results are communicated to
 * Swing using an instance of the DiskMark class.
 */

public class DiskBenchmarker {


    private DiskUI ui = null;
    private DiskBenchmarkCaller caller = null;

    public DiskBenchmarker (DiskUI ui, DiskBenchmarkCaller caller) {
	    this.ui = ui;
	    this.caller = caller;
    }

    protected Boolean runBenchmark() throws Exception {

        /*
          We 'got here' because: 1: End-user clicked 'Start' on the benchmark UI,
          which triggered the start-benchmark event associated with the App::startBenchmark()
          method.  2: startBenchmark() then instantiated a DiskWorker, and called
          its (super class's) execute() method, causing Swing to eventually
          call this doInBackground() method.
         */
        Logger.getLogger(App.class.getName()).log(Level.INFO, "*** New worker thread started ***");
        msg("Running readTest " + App.readTest + "   writeTest " + App.writeTest);
        msg("num files: " + App.numOfMarks + ", num blks: " + App.numOfBlocks
                + ", blk size (kb): " + App.blockSizeKb + ", blockSequence: " + App.blockSequence);

        ui.updateLegend();  // init chart legend info

        if (App.autoReset) {
            App.resetTestData();
            ui.resetTestData();
        }

        /*
          The UI allows a Write, Read, or both types of BMs to be started. They are done serially.
         */
        if (App.writeTest) {
            new WriteBenchmarker(ui, caller, App.numOfMarks, App.numOfBlocks, App.blockSizeKb, App.blockSequence).run();
        }

        /*
          Most benchmarking systems will try to do some cleanup in between 2 benchmark operations to
          make it more 'fair'. For example a networking benchmark might close and re-open sockets,
          a memory benchmark might clear or invalidate the Op Systems TLB or other caches, etc.
         */

        // try renaming all files to clear catch
        if (App.readTest && App.writeTest && !caller.isCancelled()) {
		ui.sendPlainMessage("""
                            For valid READ measurements please clear the disk cache by
                            using the included RAMMap.exe or flushmem.exe utilities.
                            Removable drives can be disconnected and reconnected.
                            For system drives use the WRITE and READ operations\s
                            independantly by doing a cold reboot after the WRITE""",
                    "Clear Disk Cache Now");
        }

        // Same as above, just for Read operations instead of Writes.
        if (App.readTest) {
            new ReadBenchmarker(ui, caller, App.numOfMarks, App.numOfBlocks, App.blockSizeKb, App.blockSequence).run();
        }
        App.nextMarkNumber += App.numOfMarks;
        return true;
    }
}
