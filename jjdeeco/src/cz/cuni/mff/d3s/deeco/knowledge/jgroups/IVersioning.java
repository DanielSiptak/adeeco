package cz.cuni.mff.d3s.deeco.knowledge.jgroups;

import java.io.Serializable;

public interface IVersioning extends Serializable {
	
	/**
	 * Returns int for which it is true that newer version will have bigger integer
	 * @param another
	 * @return
	 */
	public int getVersion();
	
}
