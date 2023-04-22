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

	private boolean legal(int e) {
		if(e<0 || e>memory.MEMORY_SIZE){
			return false;
		}
		return true;
	}

	public void run() {
		while (true) {
			if (legal(pc)) {
				ir = memory.get(pc);
				if (debugMode) { System.out.print("                               pc: "+pc+"       exec: ");  mem.dump(ir); }
				switch (ir.opcode) {

					case LDI: // Rd ← k
						registers[ir.r1] = ir.param;
						pc++;
						break;

					case LDD:
						if (legal(ir.param)) {
						   registers[ir.r1] = memory.get(ir.param).param;
						   pc++;
						}
						break;

					case LDX:
						if (legal(registers[ir.r2])) {
							registers[ir.r1] = memory.get(registers[ir.r2]).param;
							pc++;
						}
						break;

					case STD:
						if (legal(ir.param)) {
							memory.get(ir.param).opc = Opcode.DATA;
							memory.get(ir.param).param = registers[ir.r1];
							pc++;
						};
						break;

					case STX:
						if (legal(registers[ir.r1])) {
							memory.get(registers[ir.r1]).opc = Opcode.DATA;
							memory.get(registers[ir.r1]).param = registers[ir.r2];
							pc++;
						};
						break;

					case MOVE:
						registers[ir.r1] = registers[ir.r2];
						pc++;
						break;

					case ADD:
						registers[ir.r1] = registers[ir.r1] + registers[ir.r2];
						testOverflow(registers[ir.r1]);
						pc++;
						break;

					case ADDI:
						registers[ir.r1] = registers[ir.r1] + ir.param;
						testOverflow(registers[ir.r1]);
						pc++;
						break;

					case SUB:
						registers[ir.r1] = registers[ir.r1] - registers[ir.r2];
						testOverflow(registers[ir.r1]);
						pc++;
						break;

					case SUBI:
						registers[ir.r1] = registers[ir.r1] - ir.param;
						testOverflow(registers[ir.r1]);
						pc++;
						break;

					case MULT:
						registers[ir.r1] = registers[ir.r1] * registers[ir.r2];
						testOverflow(registers[ir.r1]);
						pc++;
						break;

					case JMP:
						pc = ir.param;
						break;

					case JUPI:
						pc = ir.r1;
						break;

					case JMPIG:
						if (registers[ir.r2] > 0) {
							pc = registers[ir.r1];
						} else {
							pc++;
						}
						break;

					case JMPIGK:
						if (registers[ir.r2] > 0) {
							pc = ir.param;
						} else {
							pc++;
						}
						break;

					case JMPILK:
						 if (registers[ir.r2] < 0) {
							pc = ir.param;
						} else {
							pc++;
						}
						break;

					case JMPIEK:
							if (registers[ir.r2] == 0) {
								pc = ir.param;
							} else {
								pc++;
							}
						break;


					case JMPIL:
							 if (registers[ir.r2] < 0) {
								pc = registers[ir.r1];
							} else {
								pc++;
							}
						break;

					case JMPIE:
							 if (registers[ir.r2] == 0) {
								pc = registers[ir.r1];
							} else {
								pc++;
							}
						break;

					case JMPIM:
							 pc = memory.get(ir.param].param;
						 break;

					case JMPIGM:
							 if (registers[ir.r2] > 0) {
								pc = memory.get(ir.param].param;
							} else {
								pc++;
							}
						 break;

					case JMPILM:
							 if (registers[ir.r2] < 0) {
								pc = memory.get(ir.param].param;
							} else {
								pc++;
							}
						 break;

					case JMPIEM:
							if (registers[ir.r2] == 0) {
								pc = memory.get(ir.param].param;
							} else {
								pc++;
							}
						 break;

					case JMPIGT:
							if (registers[ir.r1] > registers[ir.r2]) {
								pc = ir.param;
							} else {
								pc++;
							}
						 break;

					case STOP:
						irpt = Interrupts.STOP;
						break;

					case DATA:
						irpt = Interrupts.INVALID_INSTRUCTION;
						break;

					case TRAP:
						 sysCall.handle();
						 pc++;
						 break;

					default:
						irpt = Interrupts.INVALID_INSTRUCTION;
						break;
				}
			}

			if (!(irpt == Interrupts.noInterrupt)) {
				ih.handle(irpt,pc);
				break;
			}
		}
	}
}

