/*******************************************************************************
 * Copyright 2014 Charles University in Prague
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package cz.cuni.mff.d3s.deeco.knowledge.jgroups;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.jgroups.Channel;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;

import cz.cuni.mff.d3s.deeco.exceptions.KRExceptionAccessError;
import cz.cuni.mff.d3s.deeco.exceptions.KRExceptionUnavailableEntry;
import cz.cuni.mff.d3s.deeco.knowledge.ISession;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeRepository;
import cz.cuni.mff.d3s.deeco.knowledge.jgroups.ReplicatedHashMap.Notification;
import cz.cuni.mff.d3s.deeco.knowledge.local.DeepCopy;
import cz.cuni.mff.d3s.deeco.scheduling.ETriggerType;
import cz.cuni.mff.d3s.deeco.scheduling.IKnowledgeChangeListener;
import cz.cuni.mff.ms.siptak.adeecolib.service.AppMessenger;
import cz.cuni.mff.ms.siptak.adeecolib.service.AppMessenger.AppLogger;

/**
 * Implementation of the knowledge repository using a ReplicateHashMap.
 * 
 * @author Daniel Siptak
 * 
 */
public class ReplicatedKnowledgeRepository extends KnowledgeRepository {

	final ReentrantLock lock = new ReentrantLock();
	//private ReplicatedHashMap<String, MergingValueHolder<LinkedList<Object>>> map;// = new HashMap<String, List<Object>>();
	private ReplicatedHashMap<String, ReplicatedList<Object>> map;// = new HashMap<String, List<Object>>();
	private Channel channel = null;

	private AppLogger logger = AppMessenger.getInstance().getLogger("ReplicatedHashMap");
	private Map<IKnowledgeChangeListener,KnowledgeNotification> listeners = new ConcurrentHashMap<IKnowledgeChangeListener,KnowledgeNotification>(); 
	
	public ReplicatedKnowledgeRepository() {
		try {
			channel = new JChannel();
			channel.connect("Adeeco");
		
			//ReplicatedHashMap<String, MergingValueHolder<LinkedList<Object>>> replMap = new ReplicatedHashMap<String, MergingValueHolder<LinkedList<Object>>>(channel);
			ReplicatedHashMap<String, ReplicatedList<Object>> replMap = new ReplicatedHashMap<String, ReplicatedList<Object>>(channel);
			replMap.start(10000);
			// If synchronized facade needed
			//map = ReplicatedHashMap.synchronizedMap(replMap);
			map = replMap;
			logger.addLog("Creating ReplicatedHashMap");
		} catch (ChannelException e) {
			map=null;
			logger.addLog("Error with ReplicatedHashMap");
			e.printStackTrace();
		}
	}

	@Override
	public Object[] get(String entryKey, ISession session)
			throws KRExceptionUnavailableEntry, KRExceptionAccessError {
		/*
		MergingValueHolder<LinkedList<Object>> holder = map.get(entryKey);
		if (holder == null) {
			throw new KRExceptionUnavailableEntry("Key " + entryKey
					+ " is not in the knowledge repository.");
		}
		List<Object> vals = holder.get();
		*/
		List<Object> vals = map.get(entryKey);
		if (vals == null) {
			throw new KRExceptionUnavailableEntry("Key " + entryKey
					+ " is not in the knowledge repository.");
		}
		vals= (List<Object>) DeepCopy.copy(vals);
		return vals.toArray();
	}

	@Override
	public void put(String entryKey, Object value, ISession session)
			throws KRExceptionAccessError {
		ReplicatedList<Object> vals = map.get(entryKey);
		if (vals == null) {
			vals = new ReplicatedList<Object>();
		}
		vals.add(value);
		map.put(entryKey, vals);
	}

	@Override
	public Object[] take(String entryKey, ISession session)
			throws KRExceptionUnavailableEntry, KRExceptionAccessError {
		ReplicatedList<Object> vals = map.get(entryKey);
		
		if (vals == null) {
			throw new KRExceptionUnavailableEntry("Key " + entryKey
					+ " is not in the knowledge repository.");
		}

		if (vals.size() <= 1) {
			vals.clear();
			map.replace(entryKey,vals);
		}

		return vals.toArray();
	}

	@Override
	public ISession createSession() {
		return new ReplicatedSession(this);
	}

	@Override
	public boolean registerListener(IKnowledgeChangeListener listener) {
		if (!listeners.containsKey(listener)){
			KnowledgeNotification notification = new KnowledgeNotification(listener);
			listeners.put(listener, notification);
			map.addNotifier(notification);
		}
		return false;
	}

	@Override
	public void setListenersActive(boolean on) {
		map.setNotifierEnabled(on);
	}

	@Override
	public boolean isListenersActive() {
		return map.isNotifierEnabled();
	}

	@Override
	public boolean unregisterListener(IKnowledgeChangeListener listener) {
		if (listeners.containsKey(listener)){
			KnowledgeNotification notification = listeners.get(listener);
			 map.removeNotifier(notification);
			 listeners.remove(listener);
			 return true;
		} else {
			return false;
		}
	}

	/**
	 * TODO
	 * This method is just for TestReplication scenario 
	 * prints list off all keys and values stored in map 
	 */
	public void printAll() {
		
		Iterator<String> iterator = map.keySet().iterator();
	    while(iterator.hasNext()) {
	        String key = iterator.next();
	    	System.out.println(key+" : "+map.get(key));
	    }
		
	}

}
