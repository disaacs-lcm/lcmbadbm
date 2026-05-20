package edu.touro.mco152.bm.command;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.Properties;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import edu.touro.mco152.bm.*;
import edu.touro.mco152.bm.ui.*;
import edu.touro.mco152.bm.persist.DiskRun.BlockSequence;

public class BenchmarkerTest {
	private int numFiles = 25, numBlks = 128, blkSizeKb = 2048;
	private BlockSequence blockSequence = BlockSequence.SEQUENTIAL;
	private static DiskUI ui;
	private static mockCaller caller;
	BenchmarkCommand bmCommand;

	@BeforeAll
	public static void setUp() {
		/// Do the minimum of what App.init() would do to allow to run.
		Gui.mainFrame = new MainFrame();
		App.p = new Properties();
		App.loadConfig();

		Gui.progressBar = Gui.mainFrame.getProgressBar(); // must be set or get Nullptr

		// configure the embedded DB in .jDiskMark
		System.setProperty("derby.system.home", App.APP_CACHE_DIR);

		// code from startBenchmark
		// 4. create data dir reference

		// may be null when tests not run in original proj dir, so use a default area
		if (App.locationDir == null) {
			App.locationDir = new File(System.getProperty("user.home"));
		}

		App.dataDir = new File(App.locationDir.getAbsolutePath() + File.separator + App.DATADIRNAME);

		// 5. remove existing test data if exist
		if (App.dataDir.exists()) {
			if (App.dataDir.delete()) {
				App.msg("removed existing data dir");
			} else {
				App.msg("unable to remove existing data dir");
			}
		} else {
			App.dataDir.mkdirs(); // create data dir if not already present
		}

		ui = new mockUI();
		caller = new mockCaller();
	}

	@Test
	public void writeBenchmark() {
		bmCommand = new WriteBenchmarker(ui, caller, numFiles, numBlks, blkSizeKb, blockSequence);
		bmCommand.run();
		assertFalse(caller.getMarks().isEmpty());
	}

	@Test
	public void readBenchmark() {
		bmCommand = new ReadBenchmarker(ui, caller, numFiles, numBlks, blkSizeKb, blockSequence);
		bmCommand.run();
		assertFalse(caller.getMarks().isEmpty());
	}
}
