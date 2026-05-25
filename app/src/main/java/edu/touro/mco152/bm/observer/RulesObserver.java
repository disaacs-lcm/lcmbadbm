package edu.touro.mco152.bm.observer;

import java.util.ArrayList;
import java.util.List;

import edu.touro.mco152.bm.BenchmarkObserver;
import edu.touro.mco152.bm.persist.DiskRun;

public class RulesObserver implements BenchmarkObserver {
	List<Rule> rules = new ArrayList<>();

	public void addRule(Rule r) {
		rules.add(r);
	}

	@Override
	public void update(DiskRun run) {
		for (Rule r : rules) {
			if (r.conditionMet(run))
				r.execute(run);
		}
	}
}
