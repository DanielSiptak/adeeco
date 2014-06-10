package cz.cuni.mff.d3s.events;

public interface IEventBus {

	/**
	 * Before registering any subscribers, use this method to configure if EventBus should log exceptions thrown by
	 * subscribers (default: true).
	 */
	public void configureLogSubscriberExceptions(boolean logSubscriberExceptions);

	/**
	 * Registers the given subscriber to receive events. Subscribers must call {@link #unregister(Object)} once they are
	 * no longer interested in receiving events.
	 * 
	 * Subscribers have event handling methods that are identified by their name, typically called "onEvent". Event
	 * handling methods must have exactly one parameter, the event. If the event handling method is to be called in a
	 * specific thread, a modifier is appended to the method name. Valid modifiers match one of the {@link ThreadMode}
	 * enums. For example, if a method is to be called in the UI/main thread by EventBus, it would be called
	 * "onEventMainThread".
	 */
	public void register(Object subscriber);

	/**
	 * Like {@link #register(Object)} with an additional subscriber priority to influence the order of event delivery.
	 * Within the same delivery thread ({@link ThreadMode}), higher priority subscribers will receive events before
	 * others with a lower priority. The default priority is 0. Note: the priority does *NOT* affect the order of
	 * delivery among subscribers with different {@link ThreadMode}s!
	 */
	public void register(Object subscriber, int priority);

	/**
	 * @deprecated For simplification of the API, this method will be removed in the future.
	 */
	@Deprecated
	public void register(Object subscriber, String methodName);

	/**
	 * Like {@link #register(Object)}, but also triggers delivery of the most recent sticky event (posted with
	 * {@link #postSticky(Object)}) to the given subscriber.
	 */
	public void registerSticky(Object subscriber);

	/**
	 * Like {@link #register(Object,int)}, but also triggers delivery of the most recent sticky event (posted with
	 * {@link #postSticky(Object)}) to the given subscriber.
	 */
	public void registerSticky(Object subscriber, int priority);

	/**
	 * @deprecated For simplification of the API, this method will be removed in the future.
	 */
	@Deprecated
	public void registerSticky(Object subscriber, String methodName);

	/**
	 * @deprecated For simplification of the API, this method will be removed in the future.
	 */
	@Deprecated
	public void register(Object subscriber, Class<?> eventType,
			Class<?>... moreEventTypes);

	/**
	 * @deprecated For simplification of the API, this method will be removed in the future.
	 */
	@Deprecated
	public void register(Object subscriber, String methodName,
			Class<?> eventType, Class<?>... moreEventTypes);

	/**
	 * @deprecated For simplification of the API, this method will be removed in the future.
	 */
	@Deprecated
	public void registerSticky(Object subscriber, Class<?> eventType,
			Class<?>... moreEventTypes);

	/**
	 * @deprecated For simplification of the API, this method will be removed in the future.
	 */
	@Deprecated
	public void registerSticky(Object subscriber, String methodName,
			Class<?> eventType, Class<?>... moreEventTypes);

	public boolean isRegistered(Object subscriber);

	/**
	 * @deprecated For simplification of the API, this method will be removed in the future.
	 */
	@Deprecated
	public void unregister(Object subscriber, Class<?>... eventTypes);

	/** Unregisters the given subscriber from all event classes. */
	public void unregister(Object subscriber);

	/** Posts the given event to the event bus. */
	public void post(Object event);

	/**
	 * Called from a subscriber's event handling method, further event delivery will be canceled. Subsequent subscribers
	 * won't receive the event. Events are usually canceled by higher priority subscribers (see
	 * {@link #register(Object, int)}). Canceling is restricted to event handling methods running in posting thread
	 * {@link ThreadMode#PostThread}.
	 */
	public void cancelEventDelivery(Object event);

	/**
	 * Posts the given event to the event bus and holds on to the event (because it is sticky). The most recent sticky
	 * event of an event's type is kept in memory for future access. This can be {@link #registerSticky(Object)} or
	 * {@link #getStickyEvent(Class)}.
	 */
	public void postSticky(Object event);

	/**
	 * Gets the most recent sticky event for the given type.
	 * 
	 * @see #postSticky(Object)
	 */
	public <T> T getStickyEvent(Class<T> eventType);

	/**
	 * Remove and gets the recent sticky event for the given event type.
	 * 
	 * @see #postSticky(Object)
	 */
	public <T> T removeStickyEvent(Class<T> eventType);

	/**
	 * Removes the sticky event if it equals to the given event.
	 * 
	 * @return true if the events matched and the sticky event was removed.
	 */
	public boolean removeStickyEvent(Object event);

	/**
	 * Removes all sticky events.
	 */
	public void removeAllStickyEvents();

}