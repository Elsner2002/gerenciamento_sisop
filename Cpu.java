public class Cpu {
	public static final int MIN_INT = -32767;
	public static final int MAX_INT = 32767;
	private CpuState state;
	private boolean trace = false;
	private Process processRunning;

	private boolean testForOverflow(int v) {
		if (v < Cpu.MIN_INT || v > Cpu.MAX_INT) {
			this.state.setIrpt(Interrupt.OVERFLOW);
			return true;
		}

		return false;
	}

	private boolean legal(int e) {                             // todo acesso a memoria tem que ser verificado
		if(e<0 || e>memory.MEMORY_SIZE){
			return false;
		}
		return true;
	}

	public void traceOn(){
		trace = true;
	}

	public void traceOff(){
		trace = false;
	}

	public void run(Process p) { 		// execucao da CPU supoe que o contexto da CPU, vide acima, esta devidamente setado			
		processRunning = p;
		processRunning.getPcb().changeReady();
		processRunning.getPcb().changeRunning();
		while (state.isRunning()) { 			// ciclo de instrucoes. acaba cfe instrucao, veja cada caso.
		   // --------------------------------------------------------------------------------------------------
		   // FETCH
			if (legal(pc)) { 	// pc valido
				ir = memory.get(pc); 	// <<<<<<<<<<<<           busca posicao da memoria apontada por pc, guarda em ir
				if (debugMode) { System.out.print("                               pc: "+pc+"       exec: ");  mem.dump(ir); }
		   // --------------------------------------------------------------------------------------------------
		   // EXECUTA INSTRUCAO NO ir
				switch (ir.opcode) {   // conforme o opcode (código de operação) executa

				// Instrucoes de Busca e Armazenamento em Memoria
					case LDI: // Rd ← k
						registers[ir.r1] = ir.param;
						pc++;
						break;

					case LDD: // Rd <- [A]
						if (legal(ir.param)) {
						   registers[ir.r1] = memory.get(ir.param).param;
						   pc++;
						}
						break;

					case LDX: // RD <- [RS] // NOVA
						if (legal(registers[ir.r2])) {
							registers[ir.r1] = memory.get(registers[ir.r2]).param;
							pc++;
						}
						break;

					case STD: // [A] ← Rs
						if (legal(ir.param)) {
							memory.get(ir.param).opc = Opcode.DATA;
							memory.get(ir.param).param = registers[ir.r1];
							pc++;
						};
						break;

					case STX: // [Rd] ←Rs
						if (legal(registers[ir.r1])) {
							memory.get(registers[ir.r1]).opc = Opcode.DATA;      
							memory.get(registers[ir.r1]).param = registers[ir.r2];          
							pc++;
						};
						break;
					
					case MOVE: // RD <- RS
						registers[ir.r1] = registers[ir.r2];
						pc++;
						break;	
						
				// Instrucoes Aritmeticas
					case ADD: // Rd ← Rd + Rs
						registers[ir.r1] = registers[ir.r1] + registers[ir.r2];
						testOverflow(registers[ir.r1]);
						pc++;
						break;

					case ADDI: // Rd ← Rd + k
						registers[ir.r1] = registers[ir.r1] + ir.param;
						testOverflow(registers[ir.r1]);
						pc++;
						break;

					case SUB: // Rd ← Rd - Rs
						registers[ir.r1] = registers[ir.r1] - registers[ir.r2];
						testOverflow(registers[ir.r1]);
						pc++;
						break;

					case SUBI: // RD <- RD - k // NOVA
						registers[ir.r1] = registers[ir.r1] - ir.param;
						testOverflow(registers[ir.r1]);
						pc++;
						break;

					case MULT: // Rd <- Rd * Rs
						registers[ir.r1] = registers[ir.r1] * registers[ir.r2];  
						testOverflow(registers[ir.r1]);
						pc++;
						break;

				// Instrucoes JUMP
					case JMP: // PC <- k
						pc = ir.param;
						break;
					
					case JUPI:
						pc = ir.r1;
						break;
					
					case JMPIG: // If Rc > 0 Then PC ← Rs Else PC ← PC +1
						if (registers[ir.r2] > 0) {
							pc = registers[ir.r1];
						} else {
							pc++;
						}
						break;

					case JMPIGK: // If RC > 0 then PC <- k else PC++
						if (registers[ir.r2] > 0) {
							pc = ir.param;
						} else {
							pc++;
						}
						break;

					case JMPILK: // If RC < 0 then PC <- k else PC++
						 if (registers[ir.r2] < 0) {
							pc = ir.param;
						} else {
							pc++;
						}
						break;

					case JMPIEK: // If RC = 0 then PC <- k else PC++
							if (registers[ir.r2] == 0) {
								pc = ir.param;
							} else {
								pc++;
							}
						break;


					case JMPIL: // if Rc < 0 then PC <- Rs Else PC <- PC +1
							 if (registers[ir.r2] < 0) {
								pc = registers[ir.r1];
							} else {
								pc++;
							}
						break;
	
					case JMPIE: // If Rc = 0 Then PC <- Rs Else PC <- PC +1
							 if (registers[ir.r2] == 0) {
								pc = registers[ir.r1];
							} else {
								pc++;
							}
						break; 

					case JMPIM: // PC <- [A]
							 pc = memory.get(ir.param].param;
						 break; 

					case JMPIGM: // If RC > 0 then PC <- [A] else PC++
							 if (registers[ir.r2] > 0) {
								pc = memory.get(ir.param].param;
							} else {
								pc++;
							}
						 break;  

					case JMPILM: // If RC < 0 then PC <- k else PC++
							 if (registers[ir.r2] < 0) {
								pc = memory.get(ir.param].param;
							} else {
								pc++;
							}
						 break; 

					case JMPIEM: // If RC = 0 then PC <- k else PC++
							if (registers[ir.r2] == 0) {
								pc = memory.get(ir.param].param;
							} else {
								pc++;
							}
						 break; 

					case JMPIGT: // If RS>RC then PC <- k else PC++
							if (registers[ir.r1] > registers[ir.r2]) {
								pc = ir.param;
							} else {
								pc++;
							}
						 break; 

				// outras
					case STOP: // por enquanto, para execucao
						irpt = Interrupts.STOP;
						break;

					case DATA:
						irpt = Interrupts.INVALID_INSTRUCTION;
						break;

				// Chamada de sistema
					case TRAP:
						 sysCall.handle();            // <<<<< aqui desvia para rotina de chamada de sistema, no momento so temos IO
						 pc++;
						 break;

				// Inexistente
					default:
						irpt = Interrupts.INVALID_INSTRUCTION;
						break;
				}
			}
		   // --------------------------------------------------------------------------------------------------
		   // VERIFICA INTERRUPÇÃO !!! - TERCEIRA FASE DO CICLO DE INSTRUÇÕES
			if (!(irpt == Interrupts.noInterrupt)) {   // existe interrupção
				ih.handle(irpt,pc);                       // desvia para rotina de tratamento
				break; // break sai do loop da cpu
			}
		}  // FIM DO CICLO DE UMA INSTRUÇÃO
	}
}

