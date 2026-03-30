package edu.touro.mco152.bm;

public interface BenchmarkContext {
    void reportProgress(int pct);
    void publishMark(DiskMark mark);
    boolean checkCancelled();
}
