package utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Arrays;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Strings {

	public static boolean isNullOrEmpty(String self) {
		return (self == null) || self.isEmpty();
	}

	public static boolean isNullOrBlank(String self) {
		return (self == null) || self.isBlank();
	}

	public static boolean startsWithIgnoreCase(@NonNull String self, @NonNull String s) {
		return (self.length() >= s.length()) && (self.startsWith(s) || self.startsWith(s.toLowerCase()));
	}

	public static boolean endsWithIgnoreCase(@NonNull String self, @NonNull String s) {
		return (self.length() >= s.length()) && (self.endsWith(s) || self.endsWith(s.toLowerCase()));
	}

	public static String concat(@NonNull String... strings) {
		if (strings.length == 0)
			return "";

		if (strings.length == 2)
			return strings[0].concat(strings[1]);

		var length = 0;
		for (final var s : strings)
			length += s.length();

		final var sb = new StringBuilder(length);
		for (final var s : strings)
			sb.append(s);

		return sb.toString();
	}

	public static String join(@NonNull String separator, @NonNull String... strings) {
		return (strings.length != 0)
				? Arrays.stream(strings).collect(Collectors.joining(separator))
				: "";
	}

	public static String capitalize(@NonNull String self) {
		if (self.isEmpty())
			return self;

		return self.substring(0, 1).toUpperCase() + self.substring(1, self.length());
	}

	public static String uncapitalize(@NonNull String self) {
		if (self.isEmpty())
			return self;

		return self.substring(0, 1).toLowerCase() + self.substring(1, self.length());
	}

	public static String truncate(@NonNull String self, int length, boolean ellipsis) {
		if (self.length() <= length)
			return self;

		if (ellipsis)
			return self.substring(0, length) + "…";

		return self.substring(0, length);
	}
}
