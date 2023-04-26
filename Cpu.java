public class Cpu {
	public static final int NUM_GENERAL_PURPOSE_REGS = 10;
	public static final int MIN_INT = -32767;
	public static final int MAX_INT = 32767;
	private CpuState state;
	private Memory memory;
	private boolean trace = false;
	private InterruptHandler interruptHandler;
	private SyscallHandler syscallHandler;

	public Cpu(
		Memory memory, InterruptHandler interruptHandler,
		SyscallHandler syscallHandler
	) {
		this.state = new CpuState();
		this.memory = memory;
		this.interruptHandler = interruptHandler;
		this.syscallHandler = syscallHandler;
	}
	//executa o processo passado
	public void run(Process p) {
		this.state = new CpuState();
		this.state.setFrames(p.getPcb().getFrames());

		while (true) {
			fetch();
			execute();

			if (interrupt()) {
				break;
			}
		}
	}
	//verifica se a instrução do processo tem um endereço válido
	private void fetch() {
		if (!isLegalAddress(this.state.getPc())) {
			return;
		}

		this.state.setIr(this.memory.get(this.state.getPc()));
	}
	//executa a instrução
	private void execute() {
		Word ir = this.state.getIr();
		int r1Value = -1;
		int r2Value = -1;
		boolean jump = false;

		if (ir.r1() >= 0 && ir.r1() < Cpu.NUM_GENERAL_PURPOSE_REGS) {
			r1Value = this.state.getReg(ir.r1());
		}

		if (ir.r2() >= 0 && ir.r2() < Cpu.NUM_GENERAL_PURPOSE_REGS) {
			r2Value = this.state.getReg(ir.r2());
		}

		if (trace) {
			System.out.println(ir);
		}

		switch (this.state.getIr().opcode()) {
			case ADD:
				int sum = r1Value + r2Value;

				if (!isOverflow(sum)) {
					this.state.setReg(ir.r1(), sum);
				}

				break;
			case ADDI:
				int sumI = r1Value + ir.param();

				if (!isOverflow(sumI)) {
					this.state.setReg(ir.r1(), sumI);
				}

				break;
			case JMP:
				this.state.setPc(ir.param());
				jump = true;
				break;
			case JMPI:
				this.state.setPc(r1Value);
				jump = true;
				break;
			case JMPIE:
				if (r2Value == 0) {
					this.state.setPc(r1Value);
					jump = true;
				}

				break;
			case JMPIEK:
				if (r2Value == 0) {
					this.state.setPc(ir.param());
					jump = true;
				}

				break;
			case JMPIEM:
				if (r2Value == 0 && isLegalAddress(ir.param())) {
					Word word = memory.get(translateToPhysical(ir.param()));
					this.state.setPc(word.param());
					jump = true;
				}

				break;
			case JMPIG:
				if (r2Value > 0) {
					this.state.setPc(r1Value);
					jump = true;
				}

				break;
			case JMPIGK:
				if (r2Value > 0) {
					this.state.setPc(ir.param());
					jump = true;
				}

				break;
			case JMPIGM:
				if (r2Value > 0 && isLegalAddress(ir.param())) {
					Word word = memory.get(translateToPhysical(ir.param()));
					this.state.setPc(word.param());
					jump = true;
				}

				break;
			case JMPIGT:
				if (r1Value > r2Value) {
					this.state.setPc(ir.param());
					jump = true;
				}

				break;
			case JMPIL:
				if (r2Value < 0) {
					this.state.setPc(r1Value);
					jump = true;
				}

				break;
			case JMPILK:
				if (r2Value < 0) {
					this.state.setPc(ir.param());
					jump = true;
				}

				break;
			case JMPILM:
				if (r2Value < 0 && isLegalAddress(ir.param())) {
					Word word = memory.get(translateToPhysical(ir.param()));
					this.state.setPc(word.param());
					jump = true;
				}

				break;
			case JMPIM:
				if (isLegalAddress(ir.param())) {
					Word word = memory.get(translateToPhysical(ir.param()));
					this.state.setPc(word.param());
					jump = true;
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
				if (isLegalAddress(r2Value)) {
					Word word = memory.get(translateToPhysical(r2Value));
					this.state.setReg(ir.r1(), word.param());
				}

				break;
			case MOVE:
				this.state.setReg(ir.r1(), r2Value);
				break;
			case MULT:
				int mult = r1Value * r2Value;

				if (!isOverflow(mult)) {
					this.state.setReg(ir.r1(), mult);
				}

				break;
			case STD:
				if (isLegalAddress(ir.param())) {
					this.memory.set(translateToPhysical(ir.param()), r1Value);
				}

				break;
			case STOP:
				this.state.setIrpt(Interrupt.STOP);
				break;
			case STX:
				if (isLegalAddress(r1Value)) {
					this.memory.set(translateToPhysical(r1Value), r2Value);
				}

				break;
			case SUB:
				int sub = r1Value - r2Value;

				if (!isOverflow(sub)) {
					this.state.setReg(ir.r1(), sub);
				}

				break;
			case SUBI:
				int subI = r1Value + ir.param();

				if (!isOverflow(subI)) {
					this.state.setReg(ir.r1(), subI);
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
	//pega a interrupção (se ocorrer) para parar ou continuar o programa
	private boolean interrupt() {
		return interruptHandler.handle(this.state);
	}
	//traduz o endereço lógico para físico
	private int translateToPhysical(int virtual_addr) {
		int page = virtual_addr / Memory.FRAME_SIZE;
		int page_start = page * Memory.FRAME_SIZE;
		int frame = this.state.getFrames()[page];
		int frame_start = frame * Memory.FRAME_SIZE;
		int offset = virtual_addr - page_start;
		return frame_start + offset;
	}
	//verifica se o valor da overflow
	private boolean isOverflow(int v) {
		if (v < Cpu.MIN_INT || v > Cpu.MAX_INT) {
			this.state.setIrpt(Interrupt.OVERFLOW);
			return true;
		}

		return false;
	}
	//verifica se o endereço é válido para a memória utilizada pelo programa
	private boolean isLegalAddress(int addr) {
		return true;
	}

	public boolean getTrace() {
		return this.trace;
	}
	//troca o estado do trace (liga/desliga a impressão das instruções)
	public void toggleTrace() {
		this.trace = !this.trace;
	}
}
