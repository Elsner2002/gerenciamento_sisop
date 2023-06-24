import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Scanner;

public class SyscallHandler extends Thread {
	private BlockingQueue<CpuState> queue;
	private Memory memory;
	private InterruptHandler interruptHandler;

	public SyscallHandler(Memory memory, InterruptHandler interruptHandler) {
		this.queue = new LinkedBlockingQueue<>();
		this.memory = memory;
		this.interruptHandler = interruptHandler;
	}

	@Override
	public void run() {
		while (true) {
			try {
				CpuState irptState = this.queue.take();
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

	public void queueRequest(CpuState cpuState) {
		this.queue.add(cpuState);
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
