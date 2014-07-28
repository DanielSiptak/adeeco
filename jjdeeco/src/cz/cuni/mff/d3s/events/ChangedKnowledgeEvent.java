package cz.cuni.mff.d3s.events;

import java.util.Collections;
import java.util.List;

import cz.cuni.mff.d3s.deeco.knowledge.jgroups.ReplicatedList;
import cz.cuni.mff.d3s.deeco.path.grammar.PathGrammar;

public class ChangedKnowledgeEvent {
	String key;
	List<Object> value;
	int version;
	
	public ChangedKnowledgeEvent(String key,ReplicatedList<Object> value){
		this.key = key;
		this.value = Collections.unmodifiableList(value);
		this.version = value.getVersion();
	}
	
	public String getKey(){
		return this.key;
	}
	
	public List<Object> getValue(){
		return this.value;
	}
	
	public String getNameSpace(){
		if (this.key.indexOf(PathGrammar.PATH_SEPARATOR) >= 0) {
			return this.key.substring(0, this.key.indexOf(PathGrammar.PATH_SEPARATOR));	
		} else {
			return "";
		}
	}
	
	public String getId(){
		if (this.key.indexOf(PathGrammar.PATH_SEPARATOR) >= 0) {
			return this.key.substring(this.key.indexOf(PathGrammar.PATH_SEPARATOR)+1,this.key.length());	
		} else {
			return this.key;
		}
	}

	public int getVersion(){
		return version;
	}
}
