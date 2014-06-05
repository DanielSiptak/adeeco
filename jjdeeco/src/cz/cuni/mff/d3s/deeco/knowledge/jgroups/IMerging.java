package cz.cuni.mff.d3s.deeco.knowledge.jgroups;

import java.io.Serializable;

public interface IMerging extends Serializable {
	
	public void mergeWith(Serializable key,IMerging another);
}
