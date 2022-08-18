package utils;

public class Rethrowable extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public Rethrowable(Throwable cause) {
		super(cause);
	}
}
