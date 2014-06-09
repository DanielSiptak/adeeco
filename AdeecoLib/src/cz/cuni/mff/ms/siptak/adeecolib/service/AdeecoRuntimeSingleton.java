package cz.cuni.mff.ms.siptak.adeecolib.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeRepository;
import cz.cuni.mff.d3s.deeco.knowledge.LoggingKnowledgeRepository;
import cz.cuni.mff.d3s.deeco.knowledge.RepositoryKnowledgeManager;
import cz.cuni.mff.d3s.deeco.knowledge.jgroups.KnowledgeNotification;
import cz.cuni.mff.d3s.deeco.knowledge.jgroups.ReplicatedKnowledgeRepository;
import cz.cuni.mff.d3s.deeco.knowledge.jgroups.ReplicatedHashMap.Notification;
import cz.cuni.mff.d3s.deeco.knowledge.local.LocalKnowledgeRepository;
import cz.cuni.mff.d3s.deeco.provider.AbstractDEECoObjectProvider;
import cz.cuni.mff.d3s.deeco.provider.ClassDEECoObjectProvider;
import cz.cuni.mff.d3s.deeco.runtime.Runtime;
import cz.cuni.mff.d3s.deeco.scheduling.IKnowledgeChangeListener;
import cz.cuni.mff.d3s.deeco.scheduling.MultithreadedScheduler;
import cz.cuni.mff.d3s.deeco.scheduling.Scheduler;

public class AdeecoRuntimeSingleton {
	private static volatile AdeecoRuntimeSingleton INSTANCE = null;

	private Runtime rt;
	private Scheduler sched;
	private KnowledgeManager km;
	private Map<String, RuntimeBundle> bundles;
	private KnowledgeRepository kr;
	
	private void initilizeRuntime() {
		boolean replicated=true;
		if (km==null) {
			if (kr == null) {
				if (!replicated) {
					kr = new LocalKnowledgeRepository();
				} else {
				    kr = new ReplicatedKnowledgeRepository();
				}
			}
			//kr = new LoggingKnowledgeRepository(kr);
			km = new RepositoryKnowledgeManager(kr);
		} else {
			//TODO clean the km need to add it to knowledge interface
		}
		sched = new MultithreadedScheduler();
		rt = new Runtime(km, sched);
	}

	/**
	 *  private constructor to disable new
	 */
	private AdeecoRuntimeSingleton() {
		initilizeRuntime();
		bundles = new HashMap<String, RuntimeBundle>();
	}

	public synchronized static AdeecoRuntimeSingleton getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new AdeecoRuntimeSingleton();
		}
		return INSTANCE;
	}

	public void startRuntime() {
		if (!rt.isRunning()) {
			rt.startRuntime();
		}
	}

	public boolean isRunning() {
		return rt.isRunning();
	}

	public void stopRuntime() {
		rt.stopRuntime();
	}

	public void destroyRuntime() {
		rt.stopRuntime();
		initilizeRuntime(); // this will create whole new deeco runtime //TODO
		bundles.clear();
	}

	public void addRuntimeBundle(RuntimeBundle bundle) {
		// prevent to add the same bundle twice
		if (bundle != null && !isActive(bundle.getId())) {
			bundles.put(bundle.getId(), bundle);
			AbstractDEECoObjectProvider dop = new ClassDEECoObjectProvider(
					bundle.getComponents(), bundle.getEnsembles());
			rt.registerComponentsAndEnsembles(dop);
		}
	}

	// TODO need to find the way how to remove it from deeco runtime
	public void removeRuntimeBoundle(String id) {
		System.out.println("Decco bundle " + id + " was removed");
	}

	public void cleanRuntime() {
		for (String id : getBoundleIds()) {
			removeRuntimeBoundle(id);
		}
	}

	public boolean isActive(String id) {
		return bundles.containsKey(id);
	}

	public List<String> getBoundleIds() {
		return new ArrayList<String>(bundles.keySet());
	}
	
	public boolean registerListener(IKnowledgeChangeListener listener) {
		return kr.registerListener(listener);
	}

	public void setListenersActive(boolean on) {
		kr.setListenersActive(on);
	}

	public boolean isListenersActive() {
		return kr.isListenersActive();
	}

	public boolean unregisterListener(IKnowledgeChangeListener listener) {
		return kr.unregisterListener(listener);
	}
	
}
