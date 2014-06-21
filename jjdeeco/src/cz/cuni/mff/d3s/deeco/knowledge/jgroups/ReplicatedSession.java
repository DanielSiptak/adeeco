package cz.cuni.mff.d3s.deeco.knowledge.jgroups;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import cz.cuni.mff.d3s.deeco.knowledge.ISession;

class ReplicatedSession<K extends Serializable, V extends IMerging> implements ISession {

	private boolean succeeded = false;
	private final ReplicatedKnowledgeRepository kr;
	private Map<K,V> map;
	
	private Map<K,Action<K,V>> actions = new HashMap<K,Action<K,V>>();
	
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
		kr.lock.lock(); // local locking between threads
	}

	@Override
	public void end() {
		/** Late writing to replicated hash map
		 * With combination with locking by JGroups 
		 * it is possible to prevent inconsistent 
		 * knowledge on another nodes during computation  
		 */
		for (Action<K,V> action : actions.values()){
			//System.out.println("END  "+action.getKey()+" : "+action.getValue());
			this.map.put(action.getKey(),action.getValue());
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

	public V get(K key){
		Action<K,V> action = actions.get(key);
		if (action == null) {
			return map.get(key);
		} else {
			return action.getValue();
		}
	}
	
	public void put(K key,V value){
		actions.put(key,new Action<K, V>(ActionType.PUT, key, value));
		//System.out.println("PUT  "+key+" : "+value);
	}
	
	public V take(K key){
		Action<K,V> action = actions.get(key);
		V value = this.get(key);
		if ( action != null && action.getType() == ActionType.TAKE ) {
			value = null; //value was removed at least once before so return null
		}
		actions.put(key,new Action<K, V>(ActionType.TAKE, key, value));
		//System.out.println("TAKE  "+key+" : "+value);
		return value;
	}
}
