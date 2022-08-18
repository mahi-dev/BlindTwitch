package csv;

import lombok.NonNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

public class CsvWriter extends Writer {

	public static final char DEFAULT_SEPARATOR = CsvReader.DEFAULT_SEPARATOR_CHAR;

	private final BufferedWriter delegate;
	private final char separator;

	public static String quoteValue(@NonNull String value) {
		final var sb = new StringBuilder(value.length() + 8);

		sb.append(CsvReader.RowParser.DELIMITER_CHAR);

		for (var i = 0; i < value.length(); ++i) {
			final var c = value.charAt(i);

			if (c == CsvReader.RowParser.ESCAPE_CHAR)
				sb.append(c);

			else if (c == CsvReader.RowParser.DELIMITER_CHAR)
				sb.append(CsvReader.RowParser.ESCAPE_CHAR);

			sb.append(c);
		}

		sb.append(CsvReader.RowParser.DELIMITER_CHAR);
		return sb.toString();
	}

	public CsvWriter(@NonNull Writer writer, char separator) {
		this.delegate = new BufferedWriter(writer);
		this.separator = separator;
	}

	public CsvWriter(Writer writer) {
		this(writer, DEFAULT_SEPARATOR);
	}

	public void writeRow(@NonNull Iterable<String> row) throws IOException {
		final var it = row.iterator();

		if (!it.hasNext())
			return;

		writeValue(it.next());
		while (it.hasNext()) {
			this.delegate.write(this.separator);
			writeValue(it.next());
		}
		this.delegate.write("\r\n");
	}

	private void writeValue(@NonNull String value) throws IOException {
		this.delegate.write((value.indexOf(this.separator) != -1) ? quoteValue(value) : value);
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		this.delegate.write(cbuf, off, len);
	}

	@Override
	public void flush() throws IOException {
		this.delegate.flush();
	}

	@Override
	public void close() throws IOException {
		this.delegate.close();
	}
}
