package utils.storage.io;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
public class BoundedInputStream extends InputStream {

	public static final class MaximumSizeExceededException extends IOException {

		private static final long serialVersionUID = 8958482758608899780L;

		public MaximumSizeExceededException(long maximumSize) {
			super("Input stream exceeded size of " + new FileSize(maximumSize) + ".");
		}
	}

	@NonNull
	private final InputStream delegate;
	@Getter
	private final long maximumSize;
	@Getter
	private long position;

	@Override
	public int read() throws IOException {
		final var count = this.delegate.read();
		if (count != -1)
			++this.position;
		return count;
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (this.position > this.maximumSize)
			throw new MaximumSizeExceededException(this.maximumSize);

		final var count = super.read(b, off, len);

		if (count != -1)
			this.position += count;

		return count;
	}

	@Override
	public void close() throws IOException {
		this.delegate.close();
	}
}
