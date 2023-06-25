import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Thread-safe syscall request queue
 * for communication between CPU and syscall handler.
 */
public class SyscallQueue {
	private BlockingQueue<CpuState> queue;

	public SyscallQueue() {
		this.queue = new LinkedBlockingQueue<>();
	}

	public CpuState take() throws InterruptedException {
		try {
			return this.queue.take();
		} catch (InterruptedException e) {
			throw e;
		}
	}

	public void add(CpuState cpuState) {
		this.queue.add(cpuState);
	}
}
