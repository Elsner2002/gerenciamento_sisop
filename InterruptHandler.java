public class InterruptHandler {
    private ProcessManager processManager;
	//lida com a interrupção passada se existir
    public InterruptHandler(ProcessManager processManager){
        this.processManager = processManager;
    }

	public boolean handle(Process p, CpuState cpuState) {
		Interrupt irpt = cpuState.getIrpt();
		cpuState.setIrpt(null);

		if (irpt == null) {
			return false;
		}

		if (irpt == Interrupt.TIMEOUT) {
			p.getPcb().setState(ProcessState.READY);
			p.getPcb().setCpuState(cpuState);
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
