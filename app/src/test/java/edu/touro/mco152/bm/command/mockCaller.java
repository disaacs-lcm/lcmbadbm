package edu.touro.mco152.bm.command;

import java.util.ArrayDeque;
import java.util.Queue;

import edu.touro.mco152.bm.DiskBenchmarkCaller;
import edu.touro.mco152.bm.DiskMark;

public class mockCaller implements DiskBenchmarkCaller {

	private Queue<DiskMark> marks = new ArrayDeque<>();

	public Queue<DiskMark> getMarks() {
		Queue<DiskMark> toReturn = new ArrayDeque<>();
		toReturn.addAll(marks);
		marks.clear();
		return toReturn;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public void publishChunks(DiskMark... chunks) {
		for (DiskMark mark : chunks) {
			marks.add(mark);
		}

	}

	@Override
	public void setBenchmarkProgress(int progress) {

	}

}
