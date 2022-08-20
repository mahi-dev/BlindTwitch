package event;

import org.apache.commons.lang.NullArgumentException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventManager {

	private static EventManager instance;
	private final Map<String, List<EventListener>> listenerMap;

	public interface EventListenerRegistration {
		void remove();
	}

	public static EventManager getInstance() {
		if (instance == null)
			instance = new EventManager();
		return instance;
	}

	private EventManager() {
		this.listenerMap = new ConcurrentHashMap<>();
	}

	public EventListenerRegistration addEventListener(final String eventType, final EventListener listener) {
		if (eventType == null) {
			throw new NullArgumentException("eventType");
		}
		if (listener == null) {
			throw new NullArgumentException("listener");
		}

		final List<EventListener> listeners = this.listenerMap.computeIfAbsent(eventType, key -> new CopyOnWriteArrayList<>());
		listeners.add(listener);
		return () -> listeners.remove(listener);
	}

	public void fireEvent(final String eventType, final Object... args) {
		if (eventType == null) {
			throw new NullArgumentException("eventType");
		}
		if (args == null) {
			throw new NullArgumentException("args");
		}

		this.listenerMap.computeIfPresent(eventType, (key, listeners) -> {
			for (final EventListener listener : listeners) {
				listener.onNotify(eventType, args);
			}
			return listeners;
		});
	}
}