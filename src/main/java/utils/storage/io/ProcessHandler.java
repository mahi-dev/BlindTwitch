package utils.storage.io;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class ProcessHandler implements AutoCloseable {

	private final Process process;
	private final LineConsumerThread outputConsumerThread;
	private final LineConsumerThread errorConsumerThread;
	private final PrintStream printStream;
	private final ManualResetEvent manualResetEvent;

	public ProcessHandler(@NonNull Process process) {
		this.process = process;
		this.printStream = new PrintStream(process.getOutputStream(), true);
		this.outputConsumerThread = new LineConsumerThread(this.process.getInputStream());
		this.errorConsumerThread = new LineConsumerThread(this.process.getErrorStream());
		this.manualResetEvent = new ManualResetEvent(false);
	}

	public ProcessHandler onOutputLine(@NonNull Consumer<String> consumer) {
		this.outputConsumerThread.setConsumer(consumer);
		return this;
	}

	public ProcessHandler onErrorLine(@NonNull Consumer<String> consumer) {
		this.errorConsumerThread.setConsumer(consumer);
		return this;
	}

	public ProcessHandler waitForOutputLine(@NonNull String regex, long timeoutMilliseconds)
			throws InterruptedException {
		return waitForOutputLine(Pattern.compile(regex), timeoutMilliseconds);
	}

	public ProcessHandler waitForOutputLine(@NonNull Pattern pattern, long timeoutMilliseconds)
			throws InterruptedException {
		final var consumer = this.outputConsumerThread.getConsumer();
		this.outputConsumerThread.setConsumer(s -> {
			if (consumer != null)
				consumer.accept(s);

			if (pattern.matcher(s).find())
				this.manualResetEvent.set();
		});

		this.manualResetEvent.waitOne(timeoutMilliseconds);
		return this;
	}

	public ProcessHandler sleep(long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (final InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
		return this;
	}

	public ProcessHandler sendInputLine(@NonNull String line) {
		this.printStream.println(line);
		return this;
	}

	@Override
	public void close() {
		try {
			this.process.waitFor();
		} catch (final InterruptedException ex) {
			Thread.currentThread().interrupt();
		}

		if (this.outputConsumerThread != null)
			this.outputConsumerThread.close();

		if (this.errorConsumerThread != null)
			this.errorConsumerThread.close();
	}

	private static class LineConsumerThread implements AutoCloseable {

		private final BufferedReader reader;
		private final Thread thread;

		@Getter
		@Setter
		private Consumer<String> consumer;

		public LineConsumerThread(InputStream inputStream) {
			this.reader = new BufferedReader(new InputStreamReader(inputStream));
			this.thread = new Thread(() -> {
				try {
					String line;
					while ((line = this.reader.readLine()) != null)
						if (this.consumer != null)
							this.consumer.accept(line);
				} catch (final IOException ex) {
					throw new IllegalStateException(ex);
				}
			});
			this.thread.start();
		}

		@Override
		public void close() {
			if (this.thread.isInterrupted())
				return;

			this.thread.interrupt();
		}
	}
}