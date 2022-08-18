package utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Characters {

	public static boolean isAsciiDigit(int self) {
		return ((self >= '0') && (self <= '9'));
	}

	public static boolean isAsciiLetter(int self) {
		return ((self >= 'a') && (self <= 'z')) || ((self >= 'A') && (self <= 'Z'));
	}
}
