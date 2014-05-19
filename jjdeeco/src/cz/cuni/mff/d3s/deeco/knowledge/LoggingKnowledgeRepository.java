package cz.cuni.mff.d3s.deeco.knowledge;

import cz.cuni.mff.d3s.deeco.exceptions.KRExceptionAccessError;
import cz.cuni.mff.d3s.deeco.exceptions.KRExceptionUnavailableEntry;
import cz.cuni.mff.d3s.deeco.scheduling.IKnowledgeChangeListener;

public class LoggingKnowledgeRepository extends KnowledgeRepository {
	
	KnowledgeRepository decorateKr;
	
	public LoggingKnowledgeRepository(KnowledgeRepository knowledgeRepositoty) {
		this.decorateKr=knowledgeRepositoty;
	}
	
	public Object [] get(String entryKey, ISession session)
			throws KRExceptionUnavailableEntry, KRExceptionAccessError
	{
		System.out.println("get S "+entryKey.toString());
		return decorateKr.get(entryKey,session);	
	}
	
	public void put(String entryKey, Object value, ISession session)
			throws KRExceptionAccessError {
		System.out.println("put S "+entryKey.toString());
		decorateKr.put(entryKey,value,session);
	}

	public Object [] take(String entryKey, ISession session)
			throws KRExceptionUnavailableEntry, KRExceptionAccessError {
		System.out.println("take S "+entryKey.toString());
		return decorateKr.take(entryKey, session);
	}
	
	public boolean registerListener(IKnowledgeChangeListener listener) {
		System.out.println("register "+listener.getKnowledgePaths().toArray());
		return decorateKr.registerListener(listener);
	}
	
	public boolean unregisterListener(IKnowledgeChangeListener listener) {
		System.out.println("unregister "+listener.getKnowledgePaths().toArray());
		return decorateKr.unregisterListener(listener);
	}
	
	public void setListenersActive(boolean on) {
		System.out.println("activate "+on);
		decorateKr.setListenersActive(on);
	}
	
	public boolean isListenersActive() {
		System.out.println("isActivate ");
		return decorateKr.isListenersActive();
	}
	
	public ISession createSession() {
		System.out.println("create");
		return new  LoggingSession(decorateKr.createSession());
	}

	public Object [] get(String entryKey) throws KRExceptionUnavailableEntry,
			KRExceptionAccessError {
		System.out.println("get "+entryKey.toString());
		return decorateKr.get(entryKey);
	}

	public void put(String entryKey, Object value)
			throws KRExceptionAccessError {
		System.out.println("put "+entryKey.toString());
		decorateKr.put(entryKey, value);
	}

	public Object [] take(String entryKey) throws KRExceptionUnavailableEntry, KRExceptionAccessError {
		System.out.println("take "+entryKey.toString());
		return decorateKr.take(entryKey);
	}
}