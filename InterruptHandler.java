import java.util.concurrent.Semaphore;

public class InterruptHandler {
    private ProcessManager processManager;
	private Semaphore semaphore;

    public InterruptHandler(ProcessManager processManager){
        this.processManager = processManager;
		this.semaphore = new Semaphore(1, true);
    }

	public boolean handle(CpuState cpuState) {
		Interrupt irpt = cpuState.getIrpt();

		if (irpt == null) {
			return false;
		}

		this.semaphore.acquireUninterruptibly();

		switch (irpt) {
			case BLOCK:
				this.processManager.block();
				break;
			case UNBLOCK:
				this.processManager.unblock();
				break;
			case TIMEOUT:
				this.processManager.timeout();
				break;
			case STOP:
				this.processManager.killRunning();
				break;
			default:
				System.out.println("error: " + cpuState.getIrpt());
				break;
		}

		cpuState.setIrpt(null);
		this.semaphore.release();
		return true;
	}
}
