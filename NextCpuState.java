public class NextCpuState {
	private CpuState nextCpuState;

	public synchronized CpuState get() {
		CpuState nextCpuState = this.nextCpuState;
		this.nextCpuState = null;
		return nextCpuState;
	}

	public synchronized void set(CpuState nextCpuState) {
		this.nextCpuState = nextCpuState;
	}
}
