package cz.cuni.mff.d3s.deeco.knowledge;

import cz.cuni.mff.d3s.deeco.exceptions.KRExceptionAccessError;
import cz.cuni.mff.d3s.deeco.exceptions.KRExceptionUnavailableEntry;
import cz.cuni.mff.d3s.deeco.scheduling.IKnowledgeChangeListener;

public class LoggingKnowledgeRepository extends KnowledgeRepository {
	
	KnowledgeRepository decorateKr;
	Boolean printValues = false;
	
	public LoggingKnowledgeRepository(KnowledgeRepository knowledgeRepositoty) {
		this.decorateKr=knowledgeRepositoty;
	}
	
	public LoggingKnowledgeRepository(KnowledgeRepository knowledgeRepositoty,Boolean printValues) {
		this.decorateKr=knowledgeRepositoty;
		this.printValues = true;
	}
	
	private void printEntry(String message,String entryKey, Object value) {
		String val="";
		if (printValues && value!=null){
			val=" -> "+value.toString();
		}
		System.out.println(message+" : "+entryKey+val);
	}
	
	public Object [] get(String entryKey, ISession session)
			throws KRExceptionUnavailableEntry, KRExceptionAccessError
	{
		printEntry("get S", entryKey, null);
		return decorateKr.get(entryKey,session);	
	}
	
	public void put(String entryKey, Object value, ISession session)
			throws KRExceptionAccessError {
		printEntry("put S", entryKey, value);
		decorateKr.put(entryKey,value,session);
	}

	public Object [] take(String entryKey, ISession session)
			throws KRExceptionUnavailableEntry, KRExceptionAccessError {
		printEntry("take S", entryKey, null);
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
		printEntry("get", entryKey, null);
		return decorateKr.get(entryKey);
	}

	public void put(String entryKey, Object value)
			throws KRExceptionAccessError {
		printEntry("put", entryKey, value);
		decorateKr.put(entryKey, value);
	}

	public Object [] take(String entryKey) throws KRExceptionUnavailableEntry, KRExceptionAccessError {
		printEntry("take", entryKey, null);
		return decorateKr.take(entryKey);
	}
}
