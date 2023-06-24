import java.util.Scanner;

public class SyscallHandler extends Thread {
	private Memory memory;
	private InterruptHandler interruptHandler;
	private SyscallQueue syscallQueue;

	public SyscallHandler(
		Memory memory, InterruptHandler interruptHandler,
		SyscallQueue syscallQueue
	) {
		this.memory = memory;
		this.interruptHandler = interruptHandler;
		this.syscallQueue = syscallQueue;
	}

	@Override
	public void run() {
		while (true) {
			try {
				CpuState irptState = this.syscallQueue.take();
				handle(irptState);
				irptState.setIrpt(Interrupt.UNBLOCK);
				interruptHandler.handle(irptState);
			} catch (InterruptedException e) {
				System.out.println(
					"error: syscallhandler: interrupted while waiting"
				);

				return;
			}
		}
	}

	private void handle(CpuState cpuState) {
        if (cpuState.getReg(8) == 1) {
			System.out.print("system: input: ");
            Scanner in = new Scanner(System.in);
            int userInput = in.nextInt();
            int addr = cpuState.getReg(9);

			this.memory.set(
				addr, Cpu.translateToPhysical(cpuState, userInput)
			);
        } else if (cpuState.getReg(8) == 2) {
            int elementR9 = cpuState.getReg(9);

            Word storedR9 = this.memory.get(Cpu.translateToPhysical(
				cpuState, elementR9
			));

            System.out.println(storedR9);
        }
	}
}
