package cz.cuni.mff.d3s.deeco.knowledge.jgroups;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cz.cuni.mff.d3s.deeco.knowledge.ISession;

class ReplicatedSession<K extends Serializable, V extends IMerging> implements ISession {

	private boolean succeeded = false;
	private final ReplicatedKnowledgeRepository kr;
	
	private List<Action<K,V>> actions = new ArrayList<Action<K,V>>();
	
	private enum ActionType{
		PUT,TAKE
	}
	
	private class Action<K,V>{
		private K key;
		private V value;
		private ActionType type;
		
		Action(ActionType type, K key, V value){
			this.type=type;
			this.key=key;
			this.value=value;
		}
		
		public K getKey() {
			return key;
		}
		
		public V getValue(){
			return value;
		}
		public ActionType getType(){
			return type;
		}
	}
	
	ReplicatedSession(ReplicatedKnowledgeRepository kr) {
		this.kr = kr;
	}
	
	@Override
	public void begin() {
		kr.lock.lock();

		// we must break transition here because there may be no thread choices at "park/unpark"
		Thread.yield();
	}

	@Override
	public void end() {
		kr.lock.unlock();
		succeeded = true;
	}
	

	@Override
	public void cancel() {
		kr.lock.unlock();
	}

	@Override
	public boolean repeat() {
		return !succeeded;
	}

	@Override
	public boolean hasSucceeded() {
		return succeeded;
	}

	public void storePut(K key,V value){
		//TODO possibly remap take and put to replace
		actions.add(new Action<K, V>(ActionType.PUT, key, value));
	}
	
	public void storeTake(K key,V value){
		actions.add(new Action<K, V>(ActionType.TAKE, key, value));
	}
	
}
