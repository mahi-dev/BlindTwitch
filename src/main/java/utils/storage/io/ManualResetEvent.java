package utils.storage.io;

public final class ManualResetEvent {

	private final Object monitor = new Object();
	private volatile boolean set = false;

	public ManualResetEvent(boolean set) {
		this.set = set;
	}

	public void waitOne() throws InterruptedException {
		synchronized (this.monitor) {
			while (!this.set)
				this.monitor.wait();
		}
	}

	public boolean waitOne(long timeoutMilliseconds) throws InterruptedException {
		synchronized (this.monitor) {
			if (this.set)
				return true;

			while (!this.set)
				this.monitor.wait(timeoutMilliseconds);

			return this.set;
		}
	}

	public void set() {
		synchronized (this.monitor) {
			this.set = true;
			this.monitor.notifyAll();
		}
	}

	public void reset() {
		this.set = false;
	}
}
