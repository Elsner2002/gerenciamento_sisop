import java.util.concurrent.Semaphore;

public class InterruptHandler {
    private ProcessManager processManager;
	private Semaphore semaphore;

    public InterruptHandler(ProcessManager processManager){
        this.processManager = processManager;
		this.semaphore = new Semaphore(1, true);
    }

	public boolean handle(CpuState cpuState) {
		this.semaphore.acquireUninterruptibly();
		Interrupt irpt = cpuState.getIrpt();

		if (irpt == null) {
			return false;
		}

		cpuState.setIrpt(null);

		switch (irpt) {
			case BLOCK:
				this.processManager.reschedule(true);
				break;
			case UNBLOCK:
				this.processManager.unblock();
				break;
			case TIMEOUT:
				this.processManager.reschedule(false);
				break;
			case STOP:
				this.processManager.killRunning();
				break;
			default:
				System.out.println("error: " + cpuState.getIrpt());
				break;
		}

		this.semaphore.release();
		return true;
	}
}
