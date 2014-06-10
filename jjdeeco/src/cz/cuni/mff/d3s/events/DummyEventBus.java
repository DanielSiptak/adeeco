package cz.cuni.mff.d3s.events;

public class DummyEventBus implements IEventBus {

	@Override
	public void configureLogSubscriberExceptions(boolean logSubscriberExceptions) {
	}

	@Override
	public void register(Object subscriber) {
	}

	@Override
	public void register(Object subscriber, int priority) {
	}

	@Override
	@Deprecated
	public void register(Object subscriber, String methodName) {
	}

	@Override
	public void registerSticky(Object subscriber) {
	}

	@Override
	public void registerSticky(Object subscriber, int priority) {
	}

	@Override
	@Deprecated
	public void registerSticky(Object subscriber, String methodName) {
	}

	@Override
	@Deprecated
	public void register(Object subscriber, Class<?> eventType,
			Class<?>... moreEventTypes) {
	}

	@Override
	@Deprecated
	public void register(Object subscriber, String methodName,
			Class<?> eventType, Class<?>... moreEventTypes) {
	}

	@Override
	@Deprecated
	public void registerSticky(Object subscriber, Class<?> eventType,
			Class<?>... moreEventTypes) {
	}

	@Override
	@Deprecated
	public void registerSticky(Object subscriber, String methodName,
			Class<?> eventType, Class<?>... moreEventTypes) {
	}

	@Override
	public boolean isRegistered(Object subscriber) {
		return false;
	}

	@Override
	@Deprecated
	public void unregister(Object subscriber, Class<?>... eventTypes) {
	}

	@Override
	public void unregister(Object subscriber) {
	}

	@Override
	public void post(Object event) {
	}

	@Override
	public void cancelEventDelivery(Object event) {
	}

	@Override
	public void postSticky(Object event) {
	}

	@Override
	public <T> T getStickyEvent(Class<T> eventType) {
		return null;
	}

	@Override
	public <T> T removeStickyEvent(Class<T> eventType) {
		return null;
	}

	@Override
	public boolean removeStickyEvent(Object event) {
		return false;
	}

	@Override
	public void removeAllStickyEvents() {
	}

}
