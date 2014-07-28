package cz.cuni.mff.d3s.events;

public class MessageEvent {
	String key;
	String value;
	
	public MessageEvent(String key,String value){
		this.key = key;
		this.value = value;
	}
	
	public String getKey(){
		return this.key;
	}
	
	public String getValue(){
		return this.value;
	}

	public class EnsembleMessage extends MessageEvent {

		public EnsembleMessage(String key, String value) {
			super(key, value);
		}
	}

	public class ComponentMessage extends MessageEvent {

		public ComponentMessage(String key, String value) {
			super(key, value);
		}
	}
}


