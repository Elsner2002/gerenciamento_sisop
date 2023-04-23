public class InterruptHandler {
	public void handle(CpuState cpuState) {
		cpuState.setIrpt(null);
		return;
	}
}

