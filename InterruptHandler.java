public class InterruptHandler {
    private ProcessManager processManager;
	//lida com a interrupção passada se existir
    public InterruptHandler(ProcessManager processManager){
        this.processManager = processManager;
    }

	public boolean handle(CpuState cpuState) {
		Interrupt irpt = cpuState.getIrpt();

		if (irpt == null) {
			return false;
		}

		if (irpt != Interrupt.STOP) {
			System.out.println("error: " + cpuState.getIrpt());
		}

		processManager.killRunning();
		cpuState.setIrpt(null);
		return true;
	}
}
