import java.util.Scanner;
//lida com as chamadas de sistema de I/O que o programa solicita
public class SyscallHandler {
	private Memory memory;

	public SyscallHandler(Memory memory) {
		this.memory = memory;
	}

	public void handle(CpuState cpuState) {
        if (cpuState.getReg(8) == 1) {
			System.out.print("system: input: ");
            Scanner in = new Scanner(System.in);
            int userInput = in.nextInt();
            int addr = cpuState.getReg(9);

			this.memory.set(
				addr, this.translateToPhysical(cpuState, userInput)
			);
        } else if (cpuState.getReg(8) == 2) {
            int elementR9 = cpuState.getReg(9);

            Word storedR9 = this.memory.get(this.translateToPhysical(
				cpuState, elementR9
			));

            System.out.println(storedR9);
        }
	}

	//traduz o endereço lógico para físico
	private int translateToPhysical(CpuState state, int virtual_addr) {
		int page = virtual_addr / Memory.FRAME_SIZE;
		int page_start = page * Memory.FRAME_SIZE;
		int frame = state.getFrames()[page];
		int frame_start = frame * Memory.FRAME_SIZE;
		int offset = virtual_addr - page_start;
		return frame_start + offset;
	}
}
