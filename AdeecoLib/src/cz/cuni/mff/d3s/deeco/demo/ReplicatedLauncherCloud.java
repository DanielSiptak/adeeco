package cz.cuni.mff.d3s.deeco.demo;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.demo.cloud.AlertEnsemble;
import cz.cuni.mff.d3s.deeco.demo.cloud.NodeA;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager;
import cz.cuni.mff.d3s.deeco.knowledge.LoggingKnowledgeRepository;
import cz.cuni.mff.d3s.deeco.knowledge.RepositoryKnowledgeManager;
import cz.cuni.mff.d3s.deeco.knowledge.jgroups.ReplicatedKnowledgeRepository;
import cz.cuni.mff.d3s.deeco.provider.AbstractDEECoObjectProvider;
import cz.cuni.mff.d3s.deeco.provider.ClassDEECoObjectProvider;
import cz.cuni.mff.d3s.deeco.runtime.Runtime;
import cz.cuni.mff.d3s.deeco.scheduling.MultithreadedScheduler;
import cz.cuni.mff.d3s.deeco.scheduling.Scheduler;

/**
 * Main class for launching the application.
 * 
 * @author Daniel Siptak
 * 
 */
public class ReplicatedLauncherCloud {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<Class<?>> components = Arrays.asList(new Class<?>[]{NodeA.class});
		List<Class<?>> ensembles = Arrays.asList(new Class<?>[]{ AlertEnsemble.class});
		
		/*
		KnowledgeManager km = new LoggingKnowledgeManager(new RepositoryKnowledgeManager(
						new ReplicatedKnowledgeRepository()));
		*/
		
		
		KnowledgeManager km = new RepositoryKnowledgeManager(
									new LoggingKnowledgeRepository(
										new ReplicatedKnowledgeRepository()));
		/*
		KnowledgeManager km = new RepositoryKnowledgeManager(
				new ReplicatedKnowledgeRepository());
 		*/	
		Scheduler scheduler = new MultithreadedScheduler();
		AbstractDEECoObjectProvider dop = new ClassDEECoObjectProvider( components, ensembles);
		Runtime rt = new Runtime(km, scheduler);
		rt.registerComponentsAndEnsembles(dop);
		rt.startRuntime();
	}
}
