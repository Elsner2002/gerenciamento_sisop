public class Cpu {
	public static final int NUM_GENERAL_PURPOSE_REGS = 8;
	public static final int MIN_INT = -32767;
	public static final int MAX_INT = 32767;
	private CpuState state;
	private Memory memory;
	private InterruptHandler interruptHandler;
	private SyscallHandler syscallHandler;

	public Cpu() {
		this.state = new CpuState();
		this.memory = new Memory();
		this.interruptHandler = new InterruptHandler();
		this.syscallHandler = new SyscallHandler();
	}

	public void run() {
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

		this.state.setIr(memory.get(this.state.getPc()));
	}

	private void execute() {
		Word ir = this.state.getIr();
		int r1 = this.state.getReg(ir.r1());
		int r2 = this.state.getReg(ir.r2());
		int param = this.state.getReg(ir.param());
		boolean jump = false;

		switch (this.state.getIr().opcode()) {
			case ADD:
				int sum = r1 + r2;

				if (!isOverflow(sum)) {
					this.state.setReg(r1, sum);
				}

				break;
			case ADDI:
				int sumI = r1 + ir.param();

				if (!isOverflow(sumI)) {
					this.state.setReg(r1, sumI);
				}

				break;
			case JMP:
				this.state.setPc(ir.param());
				jump = true;
				break;
			case JMPI:
				this.state.setPc(ir.r1());
				jump = true;
				break;
			case JMPIE:
				if (r2 == 0) {
					this.state.setPc(r1);
					jump = true;
				}

				break;
			case JMPIEK:
				if (r2 == 0) {
					this.state.setPc(param);
					jump = true;
				}

				break;
			case JMPIEM:
				if (r2 == 0 && isLegalAddress(ir.param())) {
					Word word = memory.get(translateToPhysical(ir.param()));
					this.state.setPc(addr);
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
					this.state.setPc(addr);
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
					Word word = memory.get(translateToPhysical(ir.param()));
					this.state.setPc(addr);
				}

				break;
			case JMPIM:
				if (isLegalAddress(ir.param())) {
					Word word = memory.get(translateToPhysical(ir.param()));
					this.state.setPc(addr);
				}

				break;
			case LDD:
				if (isLegalAddress(ir.param())) {
					Word word = memory.get(translateToPhysical(ir.param()));
					this.state.setReg(ir.r1(), word.param());
				}

				break;
			case LDI:
				this.state.setReg(ir.r1(), ir.param());
				break;
			case LDX:
				if (isLegalAddress(r2)) {
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
					this.state.setReg(r1, mult);
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
				if (isLegalAddress(r1)) {
					this.memory.set(translateToPhysical(r1, r2));
				}

				break;
			case SUB:
				int sub = r1 - r2;

				if (!isOverflow(sub)) {
					this.state.setReg(r1, sub);
				}

				break;
			case SUBI:
				int subI = r1 + ir.param();

				if (!isOverflow(subI)) {
					this.state.setReg(r1, subI);
				}

				break;
			case TRAP:
				this.syscallHandler.handle(this.state);
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
		int frame = this.sate.getPages(page);
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
}
