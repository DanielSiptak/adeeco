package cz.cuni.mff.ms.siptak.adeecolib;

public class ChangedKnowledgeEvent {
	String key;
	Object value;
	
	public ChangedKnowledgeEvent(String key,Object value){
		this.key = key;
		this.value = value;
	}
	
	public String getKey(){
		return this.key;
	}
	
	public Object getValue(){
		return this.value;
	}
}
