package cz.cuni.mff.d3s.deeco.demo;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.demo.cloud.MigrationEnsemble;
import cz.cuni.mff.d3s.deeco.demo.cloud.NodeA;
import cz.cuni.mff.d3s.deeco.demo.cloud.NodeB;
import cz.cuni.mff.d3s.deeco.demo.cloud.NodeC;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager;
import cz.cuni.mff.d3s.deeco.knowledge.RepositoryKnowledgeManager;
import cz.cuni.mff.d3s.deeco.knowledge.local.LocalKnowledgeRepository;
import cz.cuni.mff.d3s.deeco.provider.AbstractDEECoObjectProvider;
import cz.cuni.mff.d3s.deeco.provider.ClassDEECoObjectProvider;
import cz.cuni.mff.d3s.deeco.runtime.Runtime;
import cz.cuni.mff.d3s.deeco.scheduling.MultithreadedScheduler;
import cz.cuni.mff.d3s.deeco.scheduling.Scheduler;

/**
 * Main class for launching the application.
 * 
 * @author Michal Kit
 * 
 */
public class LocalLauncherCloud {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<Class<?>> components = Arrays.asList(new Class<?>[]{ NodeA.class,NodeB.class, NodeC.class });
		List<Class<?>> ensembles = Arrays.asList(new Class<?>[]{ MigrationEnsemble.class });
		KnowledgeManager km = new RepositoryKnowledgeManager(
				new LocalKnowledgeRepository());
		Scheduler scheduler = new MultithreadedScheduler();
		AbstractDEECoObjectProvider dop = new ClassDEECoObjectProvider( components, ensembles);
		Runtime rt = new Runtime(km, scheduler);
		rt.registerComponentsAndEnsembles(dop);
		rt.startRuntime();
	}
}
