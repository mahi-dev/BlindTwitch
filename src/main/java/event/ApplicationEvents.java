package event;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel. PRIVATE)
public final class ApplicationEvents {
	public static final String	MESSAGE_RECEIVE	= "MESSAGE_RECEIVE";
	public static final String	START_GAME		= "START_GAME";
	public static final String	STOP_GAME		= "STOP_GAME";
}