public class CpuState {
	private int pc;
	private Word ir;
	private int[] registers;
	private Interrupt irpt;
	private int memoryBase;
	private int memoryLimit;
	private int[] pages;
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

	public int getMemoryBase() {
		return memoryBase;
	}

	public void setMemoryBase(int memoryBase) {
		this.memoryBase = memoryBase;
	}


	public int getMemoryLimit() {
		return memoryLimit;
	}

	public void setMemoryLimit(int memoryLimit) {
		this.memoryLimit = memoryLimit;
	}

	public int[] getPages() {
		return pages;
	}

	public void setPages(int[] pages) {
		this.pages = pages;
	}

	public boolean isDebugMode() {
		return debugMode;
	}

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}
}
