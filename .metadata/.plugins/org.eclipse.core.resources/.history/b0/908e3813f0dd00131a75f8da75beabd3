package cz.cuni.mff.ms.siptak.adeecolib.convoy;

import java.util.Arrays;
import java.util.List;

import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager;
import cz.cuni.mff.d3s.deeco.knowledge.RepositoryKnowledgeManager;
import cz.cuni.mff.d3s.deeco.knowledge.jgroups.ReplicatedKnowledgeRepository;
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
public class ReplicatedLauncherConvoy {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<Class<?>> components = Arrays.asList(new Class<?>[] {
				RobotLeaderComponent.class, RobotFollowerComponent.class });
		List<Class<?>> ensembles = Arrays
				.asList(new Class<?>[] { });
		KnowledgeManager km = new RepositoryKnowledgeManager(
				new ReplicatedKnowledgeRepository());
		Scheduler scheduler = new MultithreadedScheduler();
		AbstractDEECoObjectProvider dop = new ClassDEECoObjectProvider(
				components, ensembles);
		Runtime rt = new Runtime(km, scheduler);
		rt.registerComponentsAndEnsembles(dop);
		rt.startRuntime();
	}
}
