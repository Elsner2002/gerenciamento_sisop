import java.util.Scanner;

public class SyscallHandler extends Thread {
	private Memory memory;
	private InterruptHandler interruptHandler;
	private SyscallQueue syscallQueue;
	private ShellIO shellIO;

	public SyscallHandler(
		Memory memory, InterruptHandler interruptHandler,
		SyscallQueue syscallQueue, ShellIO shellIO
	) {
		this.memory = memory;
		this.interruptHandler = interruptHandler;
		this.syscallQueue = syscallQueue;
		this.shellIO = shellIO;
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
			this.input(cpuState);
        } else if (cpuState.getReg(8) == 2) {
			this.output(cpuState);
        }
	}

	private void input(CpuState cpuState) {
		int userInput = this.shellIO.getInputInt("system: input: ");
		int addr = cpuState.getReg(9);

		this.memory.set(
			addr, Cpu.translateToPhysical(cpuState, userInput)
		);
	}

	private void output(CpuState cpuState) {
		int elementR9 = cpuState.getReg(9);

		Word storedR9 = this.memory.get(Cpu.translateToPhysical(
			cpuState, elementR9
		));

		this.shellIO.println(storedR9.toString());
	}
}
