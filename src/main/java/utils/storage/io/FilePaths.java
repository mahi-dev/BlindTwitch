package utils.storage.io;

import fr.agysoft.boot.Streams;
import fr.agysoft.boot.Strings;
import fr.agysoft.boot.functional.Rethrowable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilePaths {

	public static final char EXTENSION_SEPARATOR_CHAR = '.';
	public static final String EXTENSION_SEPARATOR = String.valueOf(EXTENSION_SEPARATOR_CHAR);

	public static Path createName(@NonNull Path path, String extension) {
		return Paths.get(createName(path.toString(), extension));
	}

	public static String createName(@NonNull String name, String extension) {
		return (extension != null)
				? Strings.concat(name, EXTENSION_SEPARATOR, extension)
				: name;
	}

	public static String createUniqueIndexedName(@NonNull String name, @NonNull Set<String> existingNames) {
		if (!existingNames.contains(name))
			return name;

		final var start = getNameWithoutExtension(name) + " (";
		final var end = ")";
		final var nextIndex = existingNames
				.stream()
				.filter(fn -> fn.startsWith(start) && fn.endsWith(end))
				.map(fn -> fn.substring(start.length(), fn.length() - end.length()))
				.mapToInt(s -> {
					try {
						return Integer.parseUnsignedInt(s);
					} catch (final NumberFormatException ex) {
						return -1;
					}
				})
				.max()
				.orElse(0) + 1;

		return createName(start + nextIndex + end, getExtension(name));
	}

	public static String getName(@NonNull Path path) {
		return path.getFileName().toString();
	}

	public static String getName(@NonNull String path) {
		return getName(Paths.get(path));
	}

	public static String getNameWithoutExtension(@NonNull Path path) {
		return getNameWithoutExtension(path.toString());
	}

	public static String getNameWithoutExtension(@NonNull String path) {
		final var name = getName(path);
		final int index = name.lastIndexOf(EXTENSION_SEPARATOR_CHAR);
		return (index != -1) ? name.substring(0, index) : name;
	}

	public static String getPathWithoutExtension(@NonNull Path path) {
		return getPathWithoutExtension(path.toString());
	}

	public static String getPathWithoutExtension(@NonNull String path) {
		final int index = path.lastIndexOf(EXTENSION_SEPARATOR_CHAR);
		return (index > 1) ? path.substring(0, index) : path;
	}

	public static String getExtension(@NonNull Path path) {
		return getExtension(path.toString());
	}

	public static String getExtension(@NonNull String path) {
		final var name = getName(path);
		final int index = name.lastIndexOf(EXTENSION_SEPARATOR_CHAR);
		return (index != -1) ? name.substring(index + 1) : null;
	}

	public static boolean hasExtension(@NonNull Path path, String extension) {
		return hasExtension(path.toString(), extension);
	}

	public static boolean hasExtension(@NonNull String path, String extension) {
		return (extension != null)
				? extension.equalsIgnoreCase(getExtension(path))
				: (getExtension(path) == null);
	}

	public static Path replaceExtension(@NonNull Path path, String extension) {
		return path.getParent().resolve(createName(getNameWithoutExtension(path), extension));
	}

	public static Path replaceExtension(@NonNull String path, String extension) {
		return replaceExtension(Paths.get(path), extension);
	}

	public static Stream<Path> findAll(@NonNull Iterable<Path> paths, BiPredicate<Path, BasicFileAttributes> matcher)
			throws IOException {
		try {
			return Streams.of(paths).reduce(
					Stream.<Path>empty(),
					(acc, path) -> {
						try {
							return Stream.concat(java.nio.file.Files.find(path, Integer.MAX_VALUE, matcher), acc);
						} catch (final IOException ex) {
							throw new Rethrowable(ex);
						}
					},
					Stream::concat);
		} catch (final Rethrowable ex) {
			throw (IOException) ex.getCause();
		}
	}

	public static boolean tryDelete(Path path) throws IOException {
		try {
			delete(path);
			return true;
		} catch (final NoSuchFileException ex) {
			return false;
		}
	}

	public static void delete(@NonNull Path path) throws IOException {
		if (java.nio.file.Files.isRegularFile(path)) {
			java.nio.file.Files.delete(path);
			return;
		}

		java.nio.file.Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attributes)
					throws IOException {
				java.nio.file.Files.delete(path);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path path, IOException ex) throws IOException {
				java.nio.file.Files.delete(path);
				return FileVisitResult.CONTINUE;
			}
		});
	}
}
