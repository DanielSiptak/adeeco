package cz.cuni.mff.d3s.deeco.knowledge.jgroups;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.jgroups.View;

import cz.cuni.mff.d3s.deeco.knowledge.jgroups.ReplicatedHashMap.Notification;
import cz.cuni.mff.d3s.deeco.scheduling.ETriggerType;
import cz.cuni.mff.d3s.deeco.scheduling.IKnowledgeChangeListener;

public class KnowledgeNotification implements Notification{

	IKnowledgeChangeListener listener;
	
	public KnowledgeNotification(IKnowledgeChangeListener listener) {
		this.listener = listener;
	}

	public IKnowledgeChangeListener getListener() {
		return listener;
	}
	
	@Override
	public void entrySet(Serializable key, IVersioning value) {
		List<String> paths = listener.getKnowledgePaths();
		if (paths.contains(key)) {
			listener.knowledgeChanged(key.toString(), ETriggerType.KNOWLEDGE);
		}
	}

	@Override
	public void entryReplaced(Serializable key, IVersioning value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void entryRemoved(Serializable key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void viewChange(View view, Vector mbrs_joined, Vector mbrs_left) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contentsSet(Map new_entries) {
		for (String key : (Collection<String>) new_entries.keySet()) {
			entrySet(key, (IVersioning) new_entries.get(key));
		}
	}

	@Override
	public void contentsCleared() {
		// TODO Auto-generated method stub
		
	}

}
