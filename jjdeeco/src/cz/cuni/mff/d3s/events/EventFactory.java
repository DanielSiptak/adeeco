package cz.cuni.mff.d3s.events;

public class EventFactory {
	
	private static volatile IEventBus bus;
	
	public static synchronized IEventBus getEventBus(){
		if (bus == null) {
			try {
				bus = (IEventBus)Class.forName("cz.cuni.mff.d3s.events.GreenRobotEventBus").newInstance();
			} catch (ClassNotFoundException e) {
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}
			if (bus == null) {
				bus = new DummyEventBus();
			}
		}
		return bus;
	}
}
