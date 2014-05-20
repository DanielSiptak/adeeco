package cz.cuni.mff.d3s.deeco.knowledge.jgroups;

import java.io.Serializable;


public interface IMergeable<T> extends Serializable {
	
	public <T> T merge(T another);
	
}
