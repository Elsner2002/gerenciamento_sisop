public class CpuState {
	//classe passa o estado da CPU e as suas informações
	private int pc;
	private Word ir;
	private int[] registers;
	private Interrupt irpt;
	private int[] frames;
	private boolean debugMode;

	public CpuState() {
		this.registers = new int[Cpu.NUM_GENERAL_PURPOSE_REGS];
	}

	public int getPc() {
		return pc;
	}

	public void setPc(int pc) {
		this.pc = pc;
	}

	public void incPc() {
		this.pc += 1;
	}

	public Word getIr() {
		return ir;
	}

	public void setIr(Word ir) {
		this.ir = ir;
	}

	public int[] getRegs() {
		return registers;
	}

	public int getReg(int i) {
		return registers[i];
	}

	public void setRegs(int[] registers) {
		this.registers = registers;
	}

	public void setReg(int register, int value) {
		this.registers[register] = value;
	}

	public Interrupt getIrpt() {
		return irpt;
	}
	public void setIrpt(Interrupt irpt) {
		this.irpt = irpt;
	}

	public boolean isDebugMode() {
		return debugMode;
	}

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}
}
