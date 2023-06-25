public class CpuState {
	private int pc;
	private Word ir;
	private int[] registers;
	private Interrupt irpt;
	private int[] frames;

	public CpuState(int[] frames) {
		this.registers = new int[Cpu.NUM_GENERAL_PURPOSE_REGS];
		this.frames = frames;
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

	public int[] getFrames() {
		return this.frames;
	}

	public void setFrames(int[] frames) {
		this.frames = frames;
	}
}
