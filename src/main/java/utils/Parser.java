package utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Parser {

	@FunctionalInterface
	public interface CharacterPredicate {

		boolean test(int c);
	}

	@FunctionalInterface
	public interface CharacterMapper {

		int map(int c);
	}

	@FunctionalInterface
	public interface ParserOperation {

		boolean parse() throws ParserException;
	}

	public static final int END_OF_STREAM_CHAR = -1;
	protected static final int[] SPECIAL_CHARS = {
			'<', '>', '=',
			'+', '-', '*', '/',
			'_', '-'
	};
	protected static final char NUMBER_NEGATIVE_SIGN_CHAR = '-';
	protected static final char NUMBER_DECIMAL_SEPARATOR_CHAR = '.';

	@Getter
	private String source;
	@Getter
	private int lineNumber;
	@Getter
	private int columnNumber;
	@Getter
	private String expected;

	private Reader reader;
	@Getter
	private int character;

	@Getter
	@Setter(AccessLevel.PROTECTED)
	private Object value;
	private TextPosition valueStartPosition;

	public TextPosition getPosition() {
		return new TextPosition(this.lineNumber, this.columnNumber - 1);
	}

	public TextLocation getValueLocation() {
		return new TextLocation(this.source, this.valueStartPosition, getPosition());
	}

	protected void setInput(@NonNull String source, @NonNull Reader reader) {
		this.lineNumber = 1;
		this.columnNumber = 0;
		this.source = source;
		this.reader = reader;
		this.value = null;
		this.valueStartPosition = TextPosition.EMPTY;

		nextCharacter();
	}

	protected void setInput(@NonNull String input) {
		setInput(Strings.truncate(input, 10, true), new StringReader(input));
	}

	protected boolean isSpace(int c) {
		return (c == ' ') || (c == '\t') || (c == '\r') || isNewLine(c);
	}

	protected boolean isNewLine(int c) {
		return (c == '\n');
	}

	protected boolean isFirstSymbolic(int c) {
		return isSymbolLetter(c) || isSpecial(c);
	}

	protected boolean isSymbolic(int c) {
		return isSymbolLetter(c) || isDigit(c) || isSpecial(c);
	}

	protected boolean isSymbolLetter(int c) {
		return Characters.isAsciiLetter((char) c);
	}

	protected boolean isLetter(int c) {
		return Characters.isAsciiLetter((char) c);
	}

	protected boolean isDigit(int c) {
		return Characters.isAsciiDigit((char) c);
	}

	protected boolean isSpecial(int c) {
		return isOneOf(SPECIAL_CHARS, c);
	}

	protected boolean isOneOf(int[] characters, int c) {
		for (final var cc : characters)
			if (cc == c)
				return true;

		return false;
	}

	protected boolean isEndOfStream(int c) {
		return (c == END_OF_STREAM_CHAR);
	}

	protected int readCharacter() {
		try {
			return this.reader.read();
		} catch (final IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	protected void nextCharacter() {
		this.character = readCharacter();
		if (this.character == END_OF_STREAM_CHAR)
			return;

		if (isNewLine(this.character)) {
			++this.lineNumber;
			this.columnNumber = 0;
		} else
			++this.columnNumber;
	}

	protected boolean skipSpaces() {
		while (isSpace(this.character))
			nextCharacter();

		return true;
	}

	protected boolean expecting(ParserOperation operation, String expected) {
		if (!operation.parse()) {
			if (expected == null)
				this.expected = expected;
			return false;
		}
		return true;
	}

	protected boolean parseMany(@NonNull CharacterPredicate predicate, boolean allowNone) {
		this.valueStartPosition = getPosition();

		if (!allowNone && !predicate.test(this.character))
			return false;

		final var sb = new StringBuilder();

		while (predicate.test(this.character)) {
			sb.append((char) this.character);
			nextCharacter();
		}

		this.value = sb.toString();
		return true;
	}

	protected boolean parseMany(@NonNull ParserOperation operation, boolean allowNone) throws ParserException {
		this.valueStartPosition = getPosition();

		if (!allowNone && !operation.parse())
			return false;

		final var list = new ArrayList<>();

		list.add(this.value);

		while (operation.parse())
			list.add(this.value);

		this.value = list;
		return true;
	}

	protected boolean parseSeparatedBy(@NonNull ParserOperation operation, char separator, boolean allowNone)
			throws ParserException {
		if (!operation.parse()) {
			if (!allowNone)
				return false;

			this.value = Collections.emptyList();
			return true;
		}

		if (!parseCharacter(separator)) {
			this.value = List.of(this.value);
			return true;
		}

		final var list = new ArrayList<>();

		list.add(this.value);

		do {
			if (!operation.parse())
				return false;

			list.add(this.value);

			if (!parseCharacter(separator)) {
				this.value = list;
				return true;
			}
		} while (true);
	}

	protected boolean parseDelimitedBy(char delimiter, char escape) throws ParserException {
		return parseDelimitedBy(delimiter, escape, c -> {
			if (c != escape)
				throw new ParserException(this, "Invalid escape sequence.");

			nextCharacter();
			return c;
		});
	}

	protected boolean parseDelimitedBy(char delimiter, char escape, @NonNull CharacterMapper escapeMapper)
			throws ParserException {
		if (!parseCharacter(delimiter))
			return false;

		final var sb = new StringBuilder();

		while ((this.character != delimiter) && !isEndOfStream(this.character)) {
			if (this.character == escape) {
				nextCharacter();
				if (this.character == delimiter)
					break;

				sb.append((char) escapeMapper.map(this.character));
				continue;
			}
			sb.append((char) this.character);
			nextCharacter();
		}

		if (isEndOfStream(this.character))
			return false;

		nextCharacter();
		this.value = sb.toString();
		return true;
	}

	protected boolean parseCharacter(char c) {
		if (this.character != c)
			return false;

		nextCharacter();
		return true;
	}

	protected boolean parseSymbol() {
		return isFirstSymbolic(this.character) && parseMany(this::isSymbolic, true);
	}

	protected boolean parseKeyword(@NonNull String value) {
		return parseSymbol() && value.equals(this.value);
	}

	protected boolean parseWord() {
		return parseMany(this::isLetter, false);
	}

	protected boolean parseUnsignedInteger() {
		if (!parseMany(this::isDigit, false))
			return false;

		this.value = Long.parseUnsignedLong((String) this.value);
		return true;
	}

	protected boolean parseInteger() {
		var negative = false;
		if (this.character == NUMBER_NEGATIVE_SIGN_CHAR) {
			nextCharacter();
			negative = true;
		}
		final var result = parseUnsignedInteger();

		if (negative)
			this.value = -((Long) this.value);

		return result;
	}

	protected boolean parseNumber() {
		if (!parseInteger())
			return false;

		final var integerPart = (Long) this.value;

		if (!parseCharacter(NUMBER_DECIMAL_SEPARATOR_CHAR))
			return true;

		if (!parseUnsignedInteger())
			return false;

		final var decimalPart = (Long) this.value;
		var digitCount = this.columnNumber - this.valueStartPosition.getColumnNumber();
		if (!isEndOfStream(this.character))
			--digitCount;

		this.value = integerPart + (((integerPart > 0) ? 1 : -1) * (decimalPart * Math.pow(10, -digitCount)));
		return true;
	}
}
