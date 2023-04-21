public class CpuState {
	private int pc;
	private Word ir;
	private int[] registers;
	private Interrupt irpt;
	private Memory memory;
	private boolean debugMode;

	public Interrupt getIrpt() {
		return irpt;
	}
	public void setIrpt(Interrupt irpt) {
		this.irpt = irpt;
	}
}
