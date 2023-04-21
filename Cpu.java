public class Cpu {
	public static final int MIN_INT = -32767;
	public static final int MAX_INT = 32767;
	private CpuState state;

	private boolean testForOverflow(int v) {
		if (v < Cpu.MIN_INT || v > Cpu.MAX_INT) {
			this.state.setIrpt(Interrupt.OVERFLOW);
			return true;
		}

		return false;
	}
}

