/**
 * 
 */
package cz.cuni.mff.d3s.deeco.knowledge.jgroups;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * @author Daniel Sipták
 *
 */
public class ReplicatedList<T> extends LinkedList<T> implements IMerging {

	private static final long serialVersionUID = 1L;
	/*
	private MergingFunctor<T> merger;
	public abstract class MergingFunctor<F>{
		public abstract void mergeWith(Serializable key, ReplicatedList<F> stored);
	}
	*/
	
	@Override
	public String toString() {
		return super.toString()+" ["+this.version+"]";
	}
	
	protected int version = 0;
	
	@Override
	public void mergeWith(Serializable key, IMerging stored) {
		if (stored instanceof ReplicatedList<?>){
			ReplicatedList<T> repStored = (ReplicatedList<T>) stored;
			System.out.println("Merge "+key+" ["+this.version+"] vs ["+repStored.version+"]");
			if (key.equals("id")) {
				Set<T> set = new HashSet<T>(this);
				set.addAll(repStored);
				this.clear();
				this.addAll(set);
			} else if (repStored.version > this.version) {
				version=Math.max(repStored.version, this.version);
				this.clear();
				this.addAll(repStored);
			}
		}
	}
	
	@Override
	public void add(int index, T element) {
		version++;
		super.add(index, element);
	}
	
	@Override
	public boolean add(T e) {
		version++;
		return super.add(e);
	}
	
	@Override
	public boolean addAll(Collection<? extends T> c) {
		version++;
		return super.addAll(c);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		version++;
		return super.addAll(index, c);
	}
	
	@Override
	public void addFirst(T e) {
		version++;
		super.addFirst(e);
	}
	
	@Override
	public void addLast(T e) {
		version++;
		super.addLast(e);
	}
	@Override
	public boolean offer(T e) {
		version++;
		return super.offer(e);
	}
	@Override
	public boolean offerFirst(T e) {
		version++;
		return super.offerFirst(e);
	}
	@Override
	public boolean offerLast(T e) {
		version++;
		return super.offerLast(e);
	}
	@Override
	public T poll() {
		version++;
		return super.poll();
	}
	@Override
	public T pollFirst() {
		version++;
		return super.pollFirst();
	}
	@Override
	public T pollLast() {
		version++;
		return super.pollLast();
	}
	@Override
	public T pop() {
		version++;
		return super.pop();
	}
	@Override
	public T set(int index, T element) {
		version++;
		return super.set(index, element);
	}
	@Override
	public boolean removeLastOccurrence(Object o) {
		version++;
		return super.removeLastOccurrence(o);
	}
	@Override
	public T remove() {
		version++;
		return super.remove();
	}
	@Override
	public T remove(int index) {
		version++;
		return super.remove(index);
	}
	@Override
	public boolean remove(Object o) {
		version++;
		return super.remove(o);
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		version++;
		return super.removeAll(c);
	}
	@Override
	public T removeFirst() {
		version++;
		return super.removeFirst();
	}
	@Override
	public boolean removeFirstOccurrence(Object o) {
		version++;
		return super.removeFirstOccurrence(o);
	}
	@Override
	public T removeLast() {
		version++;
		return super.removeLast();
	}
	@Override
	public boolean retainAll(Collection<?> c) {
		version++;
		return super.retainAll(c);
	}
	
	@Override
	public void clear() {
		version++;
		super.clear();
	}
}
