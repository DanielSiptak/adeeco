package cz.cuni.mff.ms.siptak.adeeco.service;

import java.io.Serializable;
import java.util.List;
/**
 * Immutable object containing parts needed for running deeco processes
 *  
 * @author Daniel Sipták
 *
 */
public class RuntimeBundle implements Serializable{
	
	private static final long serialVersionUID = 9181755414518729348L;
	
	private String id;
	private List<Class<?>> components;
	private List<Class<?>> ensembles;
	
	public RuntimeBundle(String id , List<Class<?>> components , List<Class<?>> ensembles){
		this.id = id;
		this.components = components;
		this.ensembles = ensembles;
	}
	
	public String getId() {
		return id;
	}
	
	public List<Class<?>> getComponents() {
		return components;
	}
	
	public List<Class<?>> getEnsembles() {
		return ensembles;
	}
	
	@Override
	public String toString() {
		return this.id;
	}
}
