public class InterruptHandler {
    private ProcessManager processManager;
	//lida com a interrupção passada se existir
    public InterruptHandler(ProcessManager processManager){
        this.processManager = processManager;
    }

	public boolean handle(CpuState cpuState) {
		Interrupt irpt = cpuState.getIrpt();
		cpuState.setIrpt(null);

		if (irpt == null) {
			return false;
		}

		if (irpt == Interrupt.TIMEOUT) {
			processManager.reschedule();
			return true;
		}

		if (irpt == Interrupt.STOP) {
			processManager.killRunning();
			return true;
		}

		System.out.println("error: " + cpuState.getIrpt());
		return true;
	}
}
