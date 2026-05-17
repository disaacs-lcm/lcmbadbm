package edu.touro.mco152.bm;

public interface DiskBenchmarkCaller {

	boolean isCancelled();

	void setBenchmarkProgress(int progress);

	void publishChunks(DiskMark... chunks);
}
