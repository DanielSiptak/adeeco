package cz.cuni.mff.d3s.deeco.knowledge.jgroups;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cz.cuni.mff.d3s.deeco.knowledge.ISession;

class ReplicatedSession<K extends Serializable, V extends IMerging> implements ISession {

	private boolean succeeded = false;
	private final ReplicatedKnowledgeRepository kr;
	private Map<K,V> map;
	
	private List<Action<K,V>> actions = new ArrayList<Action<K,V>>();
	
	private enum ActionType{
		PUT,TAKE
	}
	
	protected class Action<K,V>{
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
	
	ReplicatedSession(ReplicatedKnowledgeRepository kr, Map<K,V> map) {
		this.kr = kr;
		this.map = map;
	}
	
	@Override
	public void begin() {
		kr.lock.lock();

		// we must break transition here because there may be no thread choices at "park/unpark"
		Thread.yield();
	}

	@Override
	public void end() {
		for (Action<K,V> action : actions){
			switch (action.getType()) {
			case PUT:
				//kr.put(action.getKey(),action.getValue());
				this.map.put(action.getKey(),action.getValue());
				break;

			case TAKE:
				this.map.put(action.getKey(),action.getValue());
				break;
			}
		}
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

	public void get(){
		//TODO this has to be handled properly
		//actions.getL
	}
	
	public void put(K key,V value){
		//TODO possibly remap take and put to replace
		actions.add(new Action<K, V>(ActionType.PUT, key, value));
	}
	
	public void take(K key,V value){
		actions.add(new Action<K, V>(ActionType.TAKE, key, value));
	}
	
}
