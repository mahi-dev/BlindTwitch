package utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Streams {

	public static <T, E extends Exception> Stream<T> forReader(
			UnsafeSupplier<T, E> reader,
			UnsafeRunnable<E> closeHandler) throws E {
		final var firstElement = reader.get();
		return Stream.iterate(firstElement, Objects::nonNull, element -> {
			try {
				return reader.get();
			} catch (final Exception ex) {
				throw new Rethrowable(ex);
			}
		}).onClose(() -> {
			try {
				closeHandler.run();
			} catch (final Exception ex) {
				throw new Rethrowable(ex);
			}
		});
	}

	public static <T> Stream<T> of(@NonNull Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false);
	}

	public static <T> Stream<T> of(@NonNull Iterator<T> iterator) {
		return of(() -> iterator);
	}

	public static <T> Iterable<T> toIterable(@NonNull Stream<T> self) {
		return self::iterator;
	}
}
