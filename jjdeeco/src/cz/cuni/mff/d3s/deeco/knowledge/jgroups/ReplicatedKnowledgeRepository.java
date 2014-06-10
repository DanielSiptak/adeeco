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

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.jgroups.Address;
import org.jgroups.Channel;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.View;

import cz.cuni.mff.d3s.deeco.exceptions.KRExceptionAccessError;
import cz.cuni.mff.d3s.deeco.exceptions.KRExceptionUnavailableEntry;
import cz.cuni.mff.d3s.deeco.knowledge.ISession;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeRepository;
import cz.cuni.mff.d3s.deeco.knowledge.jgroups.ReplicatedHashMap.Notification;
import cz.cuni.mff.d3s.deeco.knowledge.local.DeepCopy;
import cz.cuni.mff.d3s.deeco.scheduling.IKnowledgeChangeListener;
import cz.cuni.mff.d3s.events.ChangedKnowledgeEvent;
import cz.cuni.mff.d3s.events.EventFactory;

/**
 * Implementation of the knowledge repository using a ReplicateHashMap.
 * 
 * @author Daniel Siptak
 * 
 */
public class ReplicatedKnowledgeRepository extends KnowledgeRepository {

	final ReentrantLock lock = new ReentrantLock();
	
	private ReplicatedHashMap<String, ReplicatedList<Object>> map;// = new HashMap<String, List<Object>>();
	private Channel channel = null;

	private Map<IKnowledgeChangeListener,KnowledgeNotification> listeners = new ConcurrentHashMap<IKnowledgeChangeListener,KnowledgeNotification>(); 
	
	public ReplicatedKnowledgeRepository() {
		try {
			System.setProperty("java.net.preferIPv4Stack" , "true");
			channel = new JChannel("assets/udp.xml");
			channel.connect("Adeeco");
			ReplicatedHashMap<String, ReplicatedList<Object>> replMap = new ReplicatedHashMap<String, ReplicatedList<Object>>(channel);
			replMap.start(10000);
			//replMap.addNotifier(new Notification<Serializable, IMerging>() {});
			Notification<String, ReplicatedList<Object>> notif = new Notification<String, ReplicatedList<Object>>(){

				@Override
				public void entrySet(String key, ReplicatedList<Object> value) {
					EventFactory.getEventBus().post(new ChangedKnowledgeEvent(key, value));
				}

				@Override
				public void entryReplaced(String key,
						ReplicatedList<Object> value) {
					
				}

				@Override
				public void entryRemoved(String key) {
					
				}

				@Override
				public void viewChange(View view, Vector<Address> mbrs_joined,
						Vector<Address> mbrs_left) {
					
				}

				@Override
				public void contentsSet(
						Map<String, ReplicatedList<Object>> new_entries) {
					
				}

				@Override
				public void contentsCleared() {
					
				}
			
			};
			replMap.addNotifier(notif);
			
			//System.out.println("Top protocol "+replMap.getChannel().getProtocolStack().findProtocol("DISCARD").getName());
			// If synchronized facade needed
			//map = ReplicatedHashMap.synchronizedMap(replMap);
			map = replMap;
		} catch (ChannelException e) {
			map=null;
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
		
	private ReplicatedSession<Serializable, IMerging> getSession(ISession session){
		return (ReplicatedSession<Serializable, IMerging>)session;
	}
	
	@Override
	public void put(String entryKey, Object value, ISession session)
			throws KRExceptionAccessError {
		ReplicatedList<Object> vals = map.get(entryKey);
		if (vals == null) {
			vals = new ReplicatedList<Object>();
		}
		vals= (ReplicatedList<Object>) DeepCopy.copy(vals);
		vals.add(value);
		//getSession(session).put(entryKey, vals);
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
			//getSession(session).take(entryKey, vals);
			map.replace(entryKey,vals);
		}
		return vals.toArray();
	}

	@Override
	public ISession createSession() {
		return new ReplicatedSession<String, ReplicatedList<Object>>(this,map);
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
