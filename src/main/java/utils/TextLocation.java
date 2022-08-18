package utils;

import lombok.Data;
import lombok.NonNull;

@Data
public class TextLocation {

	@NonNull
	private final String source;
	@NonNull
	private final TextPosition startPosition;
	@NonNull
	private final TextPosition endPosition;
	
	@Override
	public String toString() {
		return String.format("%s [%s, %s]", source, startPosition, endPosition);
	}
}