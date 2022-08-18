package utils;

import java.util.function.Consumer;

@FunctionalInterface
public interface UnsafeRunnable<E extends Exception> {

	@SuppressWarnings("unchecked")
	static <E extends Exception> void apply(Consumer<Runnable> action, UnsafeRunnable<E> consumer) throws E {
		try {
			action.accept(consumer.toRunnable());
		} catch (final Rethrowable th) {
			throw (E) th.getCause();
		}
	}

	void run() throws E;

	default Runnable toRunnable() {
		return () -> {
			try {
				run();
			} catch (final Exception ex) {
				throw new Rethrowable(ex);
			}
		};
	}
}