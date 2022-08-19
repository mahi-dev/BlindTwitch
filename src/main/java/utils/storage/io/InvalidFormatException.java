package utils.storage.io;

import java.io.IOException;

public class InvalidFormatException extends IOException {

	private static final long serialVersionUID = 3667506479818578489L;

	public InvalidFormatException(String type, String s) {
		super("Invalid " + type + " format for \"" + s + "\".");
	}
}
