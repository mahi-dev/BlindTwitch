package utils.storage.io;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.*;
import java.nio.charset.Charset;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IOStreams {

	public static final int DEFAULT_BYTE_BUFFER_LENGTH = 8192;

	public static long transfer(InputStream inputStream, OutputStream outputStream) throws IOException {
		return transfer(inputStream, outputStream, true, true);
	}

	public static long transfer(@NonNull InputStream inputStream, @NonNull OutputStream outputStream,
			boolean closeInput,
			boolean closeOutput) throws IOException {
		final var buffer = new byte[DEFAULT_BYTE_BUFFER_LENGTH];
		long totalCount = 0;

		try {
			int count;
			while ((count = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, count);
				totalCount += count;
			}
		} finally {
			if (closeInput)
				inputStream.close();

			if (closeOutput)
				outputStream.close();
		}
		return totalCount;
	}

	public static long transfer(Reader reader, Writer writer) throws IOException {
		return transfer(reader, writer, true, true);
	}

	public static long transfer(@NonNull Reader reader, @NonNull Writer writer, boolean closeReader,
			boolean closeWriter) throws IOException {
		try {
			return reader.transferTo(writer);
		} finally {
			if (closeReader)
				reader.close();

			if (closeWriter)
				writer.close();
		}
	}

	public static byte[] readBytes(@NonNull InputStream inputStream) throws IOException {
		final var outputStream = new ByteArrayOutputStream();
		transfer(inputStream, outputStream, true, false);
		return outputStream.toByteArray();
	}

	public static void writeBytes(@NonNull OutputStream outputStream, byte[] bytes) throws IOException {
		final var inputStream = new ByteArrayInputStream(bytes);
		transfer(inputStream, outputStream, true, false);
	}

	public static String readString(@NonNull InputStream inputStream, @NonNull Charset charset, int bytesCount)
			throws IOException {
		final var bytes = new byte[bytesCount];
		final int count = inputStream.read(bytes);
		return new String(bytes, 0, count, charset);
	}

	public static String readString(@NonNull InputStream inputStream) throws IOException {
		return readString(inputStream, Charset.defaultCharset());
	}

	public static void writeString(@NonNull OutputStream outputStream, @NonNull String s)
			throws IOException {
		writeString(outputStream, s, Charset.defaultCharset());
	}

	public static String readString(@NonNull InputStream inputStream, @NonNull Charset charset) throws IOException {
		return new String(readBytes(inputStream), charset);
	}

	public static void writeString(@NonNull OutputStream outputStream, @NonNull String s, @NonNull Charset charset)
			throws IOException {
		writeBytes(outputStream, s.getBytes(charset));
	}

	public static String readString(@NonNull Reader reader) throws IOException {
		final var writer = new StringWriter();
		transfer(reader, writer);
		return writer.toString();
	}

	public static void writeString(@NonNull Writer writer, @NonNull String s)
			throws IOException {
		final var reader = new StringReader(s);
		transfer(reader, writer);
	}
}
