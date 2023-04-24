public class Cpu {
	public static final int NUM_GENERAL_PURPOSE_REGS = 8;
	public static final int MIN_INT = -32767;
	public static final int MAX_INT = 32767;
	private CpuState state;
	private Memory memory;
	private boolean trace = false;
	private InterruptHandler interruptHandler;

	public Cpu(Memory memory, InterruptHandler interruptHandler) {
		this.state = new CpuState();
		this.memory = memory;
		this.interruptHandler = interruptHandler;
	}

	public void run(Process p) {
		this.state.setFrames(p.getPcb().getFrames());

		while (true) {
			fetch();
			execute();
			interrupt();
		}
	}

	private void fetch() {
		if (!isLegalAddress(this.state.getPc())) {
			return;
		}

		this.state.setIr(this.memory.get(this.state.getPc()));
	}

	private void execute() {
		Word ir = null;
		int r1 = -1;
		int r2 = -1;
		int param = -1;
		boolean jump = false;

		try {
			ir = this.state.getIr();
			r1 = this.state.getReg(ir.r1());
			r2 = this.state.getReg(ir.r2());
			param = this.state.getReg(ir.param());
		} catch (Exception e) { }

		switch (this.state.getIr().opcode()) {
			case ADD:
				int sum = r1 + r2;

				if (!isOverflow(sum)) {
					this.state.setReg(ir.r1(), sum);
				}

				break;
			case ADDI:
				int sumI = r1 + param;

				if (!isOverflow(sumI)) {
					//System.out.println(ir.r1());
					this.state.setReg(ir.r1(), sumI);
				}

				break;
			case JMP:
				this.state.setPc(param);
				jump = true;
				break;
			case JMPI:
				this.state.setPc(r1);
				jump = true;
				break;
			case JMPIE:
				if (r2 == 0) {
					this.state.setPc(r1);
					jump = true;
				}

				break;
			case JMPIEK:
				if (ir.r2() == 0) {
					this.state.setPc(param);
					jump = true;
				}

				break;
			case JMPIEM:
				if (r2 == 0 && isLegalAddress(ir.param())) {
					Word word = memory.get(translateToPhysical(param));
					this.state.setPc(word.param());
				}

				break;
			case JMPIG:
				if (r2 > 0) {
					this.state.setPc(r1);
					jump = true;
				}

				break;
			case JMPIGK:
				if (r2 > 0) {
					this.state.setPc(param);
					jump = true;
				}

				break;
			case JMPIGM:
				if (r2 > 0 && isLegalAddress(ir.param())) {
					Word word = memory.get(translateToPhysical(ir.param()));
					this.state.setPc(word.param());
				}

				break;
			case JMPIGT:
				if (r1 > r2) {
					this.state.setPc(param);
					jump = true;
				}

				break;
			case JMPIL:
				if (r2 < 0) {
					this.state.setPc(r1);
					jump = true;
				}

				break;
			case JMPILK:
				if (r2 < 0) {
					this.state.setPc(param);
					jump = true;
				}

				break;
			case JMPILM:
				if (r2 < 0 && isLegalAddress(ir.param())) {
					Word word = memory.get(translateToPhysical(param));
					this.state.setPc(word.param());
				}

				break;
			case JMPIM:
				if (isLegalAddress(ir.param())) {
					Word word = memory.get(translateToPhysical(param));
					this.state.setPc(word.param());
				}

				break;
			case LDD:
				if (isLegalAddress(ir.param())) {
					Word word = memory.get(translateToPhysical(param));
					this.state.setReg(ir.r1(), word.param());
				}

				break;
			case LDI:
				this.state.setReg(ir.r1(), param);
				break;
			case LDX:
				if (isLegalAddress(ir.r2())) {
					Word word = memory.get(translateToPhysical(r2));
					this.state.setReg(ir.r1(), word.param());
				}

				break;
			case MOVE:
				this.state.setReg(ir.r1(), r2);
				break;
			case MULT:
				int mult = r1 * r2;

				if (!isOverflow(mult)) {
					this.state.setReg(ir.r1(), mult);
				}

				break;
			case STD:
				if (isLegalAddress(ir.param())) {
					this.memory.set(translateToPhysical(ir.param()), r1);
				}

				break;
			case STOP:
				this.state.setIrpt(Interrupt.STOP);
				break;
			case STX:
				if (isLegalAddress(ir.r1())) {
					this.memory.set(translateToPhysical(ir.r1()), ir.r2());
				}

				break;
			case SUB:
				int sub = r1 - r2;
				if (!isOverflow(sub)) {
					this.state.setReg(ir.r1(), sub);
				}

				break;
			case SUBI:
				int subI = r1 + param;

				if (!isOverflow(subI)) {
					this.state.setReg(ir.r1(), subI);
				}

				break;
			case TRAP:
				SyscallHandler.handle(this.state);
				break;
			default:
				this.state.setIrpt(Interrupt.INVALID_INSTRUCTION);
				break;
		}

		if (!jump) {
			this.state.incPc();
		}
	}

	private void interrupt() {
		if (this.state.getIrpt() != null) {
			interruptHandler.handle(this.state);
		}
	}

	private int translateToPhysical(int virtual_addr) {
		int page = virtual_addr / Memory.FRAME_SIZE;
		int page_start = page * Memory.FRAME_SIZE;
		int frame = this.state.getFrames()[page];
		int frame_start = frame * Memory.FRAME_SIZE;
		int offset = virtual_addr - page_start;
		return frame_start + offset;
	}

	private boolean isOverflow(int v) {
		if (v < Cpu.MIN_INT || v > Cpu.MAX_INT) {
			this.state.setIrpt(Interrupt.OVERFLOW);
			return true;
		}

		return false;
	}

	// TODO: Adapt to paging.
	private boolean isLegalAddress(int addr) {

		 if (
		 	addr < this.state.getMemoryBase() ||
		 	addr > this.state.getMemoryLimit()
		 ) {
		 	this.state.setIrpt(Interrupt.INVALID_ADDRESS);
		 	return false;
		 }

		 return true;
	}

	public boolean getTrace() {
		return this.trace;
	}

	public void toggleTrace() {
		this.trace = !this.trace;
	}
}
