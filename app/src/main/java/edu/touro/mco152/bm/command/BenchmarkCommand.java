package edu.touro.mco152.bm.command;

import edu.touro.mco152.bm.BenchmarkObserver;

public interface BenchmarkCommand {
	public void run();

	public void registerObserver(BenchmarkObserver o);
}
