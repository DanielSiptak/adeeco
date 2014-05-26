/**
 * 
 */
package cz.cuni.mff.d3s.deeco.knowledge.jgroups;

import java.util.LinkedList;

/**
 * @author Daniel Sipták
 *
 */
public class ReplicatedList<T> extends LinkedList<T> implements IVersioning {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
/*
	private <T> ReplicatedList<T> merge(ReplicatedList<T> first,ReplicatedList<T> second){
		if (first == null) {
			return second;
		}
		if (second == null) {
			return first;
		}
		if ((first.modCount - second.modCount) >= 0) {
			return first;
		} else {
			return second;
		}
	}

	@Override
	public ReplicatedList<T> isNewer(IMergeable another) {
		if (another instanceof ReplicatedList<?>) {
			return merge(this,(ReplicatedList<T>)another);
		}
		return this;
	}
*/

	@Override
	public int getVersion() {
		return modCount;
	}

}
