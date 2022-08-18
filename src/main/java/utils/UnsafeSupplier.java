package utils;

import java.util.function.Consumer;
import java.util.function.Supplier;

@FunctionalInterface
public interface UnsafeSupplier<T, E extends Exception> {

	@SuppressWarnings("unchecked")
	static <T, E extends Exception> void apply(Consumer<Supplier<T>> action, UnsafeSupplier<T, E> supplier) throws E {
		try {
			action.accept(supplier.toSupplier());
		} catch (final Rethrowable th) {
			throw (E) th.getCause();
		}
	}

	T get() throws E;

	default Supplier<T> toSupplier() {
		return () -> {
			try {
				return get();
			} catch (final Exception ex) {
				throw new Rethrowable(ex);
			}
		};
	}
}