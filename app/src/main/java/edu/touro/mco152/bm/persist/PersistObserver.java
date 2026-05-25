package edu.touro.mco152.bm.persist;

import edu.touro.mco152.bm.BenchmarkObserver;
import jakarta.persistence.EntityManager;

/**
 * Persist observer adds diskruns to the Entity Manager.
 */
public class PersistObserver implements BenchmarkObserver {

	@Override
	public void update(DiskRun run) {
		EntityManager em = EM.getEntityManager();
		em.getTransaction().begin();
		em.persist(run);
		em.getTransaction().commit();
	}

}
