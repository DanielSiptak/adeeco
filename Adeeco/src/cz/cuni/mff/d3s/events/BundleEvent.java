package cz.cuni.mff.d3s.events;

import cz.cuni.mff.ms.siptak.adeeco.service.RuntimeBundle;

public class BundleEvent {
	public enum TYPE{ADD,REMOVE}
	
	private TYPE type;
	private RuntimeBundle bundle;
	private String id;
	
	public BundleEvent(TYPE type,RuntimeBundle bundle){
		this.type = type;
		this.bundle = bundle;
		this.id = bundle.getId();
	}
	
	public BundleEvent(TYPE type,String id){
		this.type = type;
		this.bundle = null;
		this.id = id;
	}
	
	public TYPE getType(){
		return this.type;
	}
	
	public RuntimeBundle getBundle(){
		return this.bundle;
	}
	
	public String getBundleId(){
		return this.id;
	}
}
