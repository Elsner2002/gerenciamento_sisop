public class Cpu {
	public static final int MIN_INT;
	public static final int MAX_INT;
	private int pc;
	private Word ir;
	private int[] registers;
	private Interrupt irpt;
	private Memory memory;
	private boolean debugMode;

	private bool testForOverflow(int v) {
		if (v < Cpu.MIN_INT || v > Cpu.MAX_INT) {
			this.irpt = Interrupt.OVERFLOW;
			return true;
		}

		return false;
	}
}

