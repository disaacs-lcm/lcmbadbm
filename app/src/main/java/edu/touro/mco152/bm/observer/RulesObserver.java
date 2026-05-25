package edu.touro.mco152.bm.observer;

import java.util.ArrayList;
import java.util.List;

import edu.touro.mco152.bm.BenchmarkObserver;
import edu.touro.mco152.bm.persist.DiskRun;

public class RulesObserver implements BenchmarkObserver {
	List<Rule> rules = new ArrayList<>();

	/**
	 * Add a rule to check against.
	 * 
	 * @param r
	 *          The new rule
	 */
	public void addRule(Rule r) {
		rules.add(r);
	}

	/**
	 * Loop through all {@link Rule}s, and call them if their conditions are met.
	 */
	@Override
	public void update(DiskRun run) {
		for (Rule r : rules) {
			if (r.conditionMet(run))
				r.execute(run);
		}
	}
}
