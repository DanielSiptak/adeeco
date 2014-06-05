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
public class MergingValueHolder<T> implements Comparable<MergingValueHolder<T>>, IMerging{

	private static final long serialVersionUID = 1L;
	private T value;
	private int modCount = 0;
	
	public MergingValueHolder(T value) {
		this.value = value;
	}
	/**
	 * Returns holed object 
	 * @return
	 */
	public T get(){
		return value;
	}

	/**
	 * Change stored value and increments number of writes
	 * @param value to be stored
	 */
	public MergingValueHolder<T> set(T value){
		modCount++;
		this.value = value;
		return this;
	}
	
	public String toString(){
		return value.toString() + " [" + modCount+"]";
	}
	
	/**
	 * This methods returns the value with higher write count
	 * It is used for merging views during merging partitions  
	 * 
	 * @param Daniel Sipták
	 * @return holder which should be used after merging
	 */
	public void mergeWith(MergingValueHolder<T> another){
		modCount=Math.max(this.modCount, another.modCount)+1;
		if (another!=null&&compareTo(another) < 0) {
			value=another.value;
		}
	}
	
	/**
	 * Compares number of writes for the {@link MergingValueHolder} object
	 *  @return 0 if number of writes is equal
	 *  		>0 if object has more writes that another
	 *  		<0 it object has less writes that another
	 */
	public int compareTo(MergingValueHolder<T> another) {
		if (another == null) {
			return Integer.MAX_VALUE;
		}
		return this.modCount - another.modCount;
	}
	
	@Override
	public void mergeWith(Serializable key,IMerging another) {
		if ( another instanceof MergingValueHolder<?>){
			mergeWith((MergingValueHolder<T>)another);
		}
	}
}

