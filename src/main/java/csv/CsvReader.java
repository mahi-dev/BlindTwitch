package csv;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import utils.Parser;
import utils.Streams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.stream.Stream;

public class CsvReader extends Reader {

	public static final char DEFAULT_SEPARATOR_CHAR = ',';

	@RequiredArgsConstructor
	public static class RowParser extends Parser {

		public static final char DELIMITER_CHAR = '"';
		public static final char ESCAPE_CHAR = '\\';

		private final char separator;

		@SuppressWarnings("unchecked")
		public List<String> parse(String line) {
			setInput(line);
			return parseSeparatedBy(this::parseValue, this.separator, false)
					? (List<String>) getValue()
					: null;
		}

		protected boolean parseValue() {
			skipSpaces();
			return (getCharacter() == DELIMITER_CHAR)
					? parseQuotedValue()
					: parseNonQuotedValue();
		}

		protected boolean parseQuotedValue() {
			return parseDelimitedBy(DELIMITER_CHAR, ESCAPE_CHAR);
		}

		protected boolean parseNonQuotedValue() {
			final var sb = new StringBuilder();
			int c;
			while (!isEndOfStream(c = getCharacter()) && (c != this.separator)) {
				sb.append((char) c);
				nextCharacter();
			}
			setValue(sb.toString());
			return true;
		}
	}

	private final BufferedReader delegate;
	private final RowParser rowParser;

	public CsvReader(@NonNull Reader delegate, char separator) {
		this.delegate = new BufferedReader(delegate);
		this.rowParser = new RowParser(separator);
	}

	public CsvReader(Reader reader) {
		this(reader, DEFAULT_SEPARATOR_CHAR);
	}

	public Stream<List<String>> rows() throws IOException {
		return Streams.forReader(this::readRow, this::close);
	}

	public List<String> readRow() throws IOException {
		final var line = this.delegate.readLine();
		if (line == null)
			return null;

		return this.rowParser.parse(line);
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		return this.delegate.read(cbuf, off, len);
	}

	@Override
	public void close() throws IOException {
		this.delegate.close();
	}
}
