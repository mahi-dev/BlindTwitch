package utils;

import lombok.Data;

@Data
public class TextPosition {
	
	public static final TextPosition EMPTY = new TextPosition(1, 0);

	private final int lineNumber;
	private final int columnNumber;
	
	@Override
	public String toString() {
		return String.format("(%d, %d)", lineNumber, columnNumber);
	}
}