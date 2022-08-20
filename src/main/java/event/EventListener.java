package event;

public interface EventListener {

	void onNotify(String eventType, Object[] args);
}