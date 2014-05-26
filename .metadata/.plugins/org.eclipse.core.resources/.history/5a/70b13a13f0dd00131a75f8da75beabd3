package cz.cuni.mff.d3s.deeco.knowledge.jgroups;

import cz.cuni.mff.d3s.deeco.knowledge.ISession;

class ReplicatedSession implements ISession {

	private boolean succeeded = false;
	private final ReplicatedKnowledgeRepository kr;
	
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

}
