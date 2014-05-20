/**
 * 
 */
package cz.cuni.mff.d3s.deeco.knowledge.jgroups;

import java.util.LinkedList;

/**
 * @author Daniel Sipták
 *
 */
public class ReplicatedList<T> extends LinkedList<T> implements IMergeable<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static <T> ReplicatedList<T> merge(ReplicatedList<T> first,ReplicatedList<T> second){
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
	public <T> T merge(T another) {
		T ret =(T) merge((ReplicatedList<T>) this,(ReplicatedList<T>)another);
		System.out.println("modCount : "+((ReplicatedList<T>)ret).modCount+" "+ret);
		return ret;
	}
}
