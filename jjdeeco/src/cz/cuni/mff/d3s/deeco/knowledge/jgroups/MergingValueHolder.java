package cz.cuni.mff.d3s.deeco.knowledge.jgroups;

import java.io.Serializable;

/**
 * This object is holding real value in the hash map
 * It is used for counting number of writes 
 * 
 * It is needed for merging of clusters where values with higher values are used. 
 * 
 * @author Daniel Sipták
 *
 */

public class MergingValueHolder<T> implements Comparable<MergingValueHolder<T>>, Serializable{

	private static final long serialVersionUID = 1L;
	private T obj;
	private int count = 0;
	
	public MergingValueHolder() {
	}
	public MergingValueHolder(T value) {
		obj = value;
	}
	/**
	 * Returns holed object 
	 * @return
	 */
	public T get(){
		return obj;
	}

	/**
	 * Change stored value and increments number of writes
	 * @param value to be stored
	 */
	public MergingValueHolder<T> set(T value){
		count++;
		obj = value;
		return this;
	}
	
	public String toString(){
		return obj.toString() + " " + count;
	}
	
	/**
	 * This methods returns the value with higher write count
	 * It is used for merging views during merging partitions  
	 * 
	 * @param Daniel Sipták
	 * @return holder which should be used after merging
	 */
	public MergingValueHolder<T> mergeWith(MergingValueHolder<T> another){
		if (compareTo(another) >= 0) {
			return this;
		} else {
			return another;
		}
	}
	
	/**
	 * Compares number of writes for the {@link MergingValueHolder} object
	 *  @return 0 if number of writes is equal
	 *  		>0 if object has more writes that another
	 *  		<0 it object has less writes that another
	 */
	public int compareTo(MergingValueHolder<T> another) {
		return this.count - another.count;
	}
}
