public class Cpu extends Thread {
	public static final int NUM_GENERAL_PURPOSE_REGS = 10;
	public static final int MIN_INT = -32767;
	public static final int MAX_INT = Integer.MAX_VALUE;
	/**
	 * CPU cycles before round-robin process scheduling kicks in and process
	 * is sent to the back of the queue.
	 */
	public static final int QUANTUM = 10;
	private CpuState state;
	/**
	 * Clock cycle count.
	 */
	private int clk;
	/**
	 * Whether to print every instruction computed and interruption handled.
	 * For debugging only.
	 */
	private boolean trace = false;
	/**
	 * Whether to wait one second between CPU cycles.
	 * For debugging only.
	 */
	private boolean slow = false;
	private Memory memory;
	private InterruptHandler interruptHandler;
	private NextCpuState nextCpuState;
	private SyscallQueue syscallQueue;

	public Cpu(
		Memory memory, InterruptHandler interruptHandler,
		NextCpuState nextCpuState, SyscallQueue syscallQueue
	) {
		this.memory = memory;
		this.interruptHandler = interruptHandler;
		this.nextCpuState = nextCpuState;
		this.syscallQueue = syscallQueue;
	}

	/**
	 * Translates a virtual address into a physical one.
	 *
	 * @param cpuState the current CPU state, used to get frames in use
	 * @param virtual_addr the virtual address to be translated
	 * @return the translated phyiscal address
	 */
	public static int translateToPhysical(
		CpuState cpuState, int virtual_addr
	) {
		int page = virtual_addr / Memory.FRAME_SIZE;
		int page_start = page * Memory.FRAME_SIZE;
		int frame = cpuState.getFrames()[page];
		int frame_start = frame * Memory.FRAME_SIZE;
		int offset = virtual_addr - page_start;
		return frame_start + offset;
	}

	@Override
	public void run() {
		// The CPU never stops.
		while (true) {
			if (this.slow) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					System.out.println("error: interrupted while sleeping");
				}
			}

			// Polls the process manager for next quantum of execution.
			CpuState nextState = this.nextCpuState.get();

			if (nextState == null) {
				continue;
			}

			this.state = nextState;

			// Computes the given process until interrupted.
			while (true) {
				this.clk++;
				fetch();
				execute();

				// Unless the current process was interrupted in this execution
				// cycle, if quantum has ended, stop computing.
				if (
					this.state.getIrpt() == null &&
					this.clk % Cpu.QUANTUM == 0
				) {
					this.state.setIrpt(Interrupt.TIMEOUT);
				}

				// Save current interruption information before this register
				// is cleared by the interrupt handler, just in case it is
				// needed after.
				Interrupt irpt = this.state.getIrpt();

				if (handleInterruption()) {
					// Entering this block means an interruption was handled
					// and it requires the CPU to stop computing the current
					// process.
					//
					// If the current process requested a syscall, its request
					// must be queued for the syscall handler to handle.
					if (irpt == Interrupt.BLOCK) {
						this.syscallQueue.add(this.state);
					}

					break;
				}
			}
		}
	}

	public CpuState getCpuState() {
		return this.state;
	}

	public boolean getTrace() {
		return this.trace;
	}

	public void toggleTrace() {
		this.trace = !this.trace;
	}

	public void toggleSlow() {
		this.slow = !this.slow;
	}

	/**
	 * Fetches the next instruction.
	 */
	private void fetch() {
		if (!isLegalAddress(this.state.getPc())) {
			return;
		}

		this.state.setIr(this.memory.get(Cpu.translateToPhysical(
			this.state, this.state.getPc()
		)));
	}

	/**
	 * Executes the current instruction.
	 */
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

		if (this.trace) {
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
					Word word = memory.get(
						Cpu.translateToPhysical(this.state, ir.param())
					);

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
					Word word = memory.get(
						Cpu.translateToPhysical(this.state, ir.param())
					);

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
					Word word = memory.get(
						Cpu.translateToPhysical(this.state, ir.param())
					);

					this.state.setPc(word.param());
					jump = true;
				}

				break;
			case JMPIM:
				if (isLegalAddress(ir.param())) {
					Word word = memory.get(
						Cpu.translateToPhysical(this.state, ir.param())
					);

					this.state.setPc(word.param());
					jump = true;
				}

				break;
			case LDD:
				if (isLegalAddress(ir.param())) {
					Word word = memory.get(
						Cpu.translateToPhysical(this.state, ir.param())
					);

					this.state.setReg(ir.r1(), word.param());
				}

				break;
			case LDI:
				this.state.setReg(ir.r1(), ir.param());
				break;
			case LDX:
				if (isLegalAddress(r2Value)) {
					Word word = memory.get(Cpu.translateToPhysical(
						this.state, r2Value
					));

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
					this.memory.set(
						Cpu.translateToPhysical(this.state, ir.param()),
						r1Value
					);
				}

				break;
			case STOP:
				this.state.setIrpt(Interrupt.STOP);
				break;
			case STX:
				if (isLegalAddress(r1Value)) {
					this.memory.set(
						Cpu.translateToPhysical(this.state, r1Value), r2Value
					);
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
				this.state.setIrpt(Interrupt.BLOCK);
				break;
			default:
				this.state.setIrpt(Interrupt.INVALID_INSTRUCTION);
				break;
		}

		if (!jump) {
			this.state.incPc();
		}
	}

	/**
	 * Attempt to handle an interruption.
	 *
	 * @return true if must stop computing the current process, else false
	 */
	private boolean handleInterruption() {
		if (this.state.getIrpt() != null && this.trace) {
			System.out.println(this.state.getIrpt());
		}

		return interruptHandler.handle(this.state);
	}

	/**
	 * Check if an integer is out of bounds.
	 *
	 * @param v the value to be checked
	 * @return whether it is out of bounds or not
	 */
	private boolean isOverflow(int v) {
		if (v < Cpu.MIN_INT || v > Cpu.MAX_INT) {
			this.state.setIrpt(Interrupt.OVERFLOW);
			return true;
		}

		return false;
	}

	private boolean isLegalAddress(int addr) {
		return true;
	}
}
