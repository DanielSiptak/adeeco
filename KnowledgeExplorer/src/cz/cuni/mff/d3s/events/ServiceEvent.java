package cz.cuni.mff.d3s.events;

public class ServiceEvent {
	public enum TYPE{
		/**
		 * Requesting start of the deeco runtime
		 */
		RUNTIME_START,
		/**
		 * Requesting pause of the deeco runtime
		 */
		RUNTIME_PAUSE,
		/**
		 * Information that service was started
		 */
		SERVICE_STARTED,
		/**
		 * Information that service was stopped
		 */
		SERVICE_STOPPED}
	
	TYPE type;
	
	public ServiceEvent(TYPE type){
		this.type = type;
	}
	
	public TYPE getType(){
		return this.type;
	}
}
