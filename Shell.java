import java.util.Scanner;

public class Shell {
	private static Cpu cpu;
	private static Memory memory;
	private static ProcessManager processManager;

	public static void main(String[] args) {
		Shell.memory = new Memory();
		MemoryManager memoryManager = new MemoryManager(memory);
		NextCpuState nextCpuState = new NextCpuState();
		SyscallQueue syscallQueue = new SyscallQueue();

		Shell.processManager = new ProcessManager(
			memoryManager, nextCpuState
		);

		InterruptHandler interruptHandler = new InterruptHandler(
			processManager
		);

		SyscallHandler syscallHandler = new SyscallHandler(
			memory, interruptHandler, syscallQueue
		);

		Shell.cpu = new Cpu(
			memory, interruptHandler, nextCpuState, syscallQueue
		);

		processManager.setCpu(cpu);
		Shell.cpu.start();
		syscallHandler.start();
		prompt();
	}

	private static void prompt() {
		Scanner in = new Scanner(System.in);

		while (true) {
			System.out.print("[dotti@pucrs]& ");
			String[] input = in.nextLine().split(" ");

			switch(input[0]) {
				case "new":
					Shell.new_(input[1]);
					break;
				case "kill":
					Shell.kill(input[1]);
					break;
				case "ps":
					Shell.ps();
					break;
				case "pdump":
					Shell.pdump(input[1]);
					break;
				case "mdump":
					Shell.mdump(input);
					break;
				case "runall":
					Shell.runAll();
					break;
				case "trace":
					Shell.trace();
					break;
				case "exit":
					in.close();
					return;
				case "":
					break;
				default:
					System.out.println(
						"system: command not found: " + input[0]
					);

					break;
			}
		}
	}

	private static void new_(String name) {
		try {
			Shell.processManager.createProcess(
				Programs.get(name)
			);
		} catch (Exception e) {
			System.out.println("new: unknown program");
		}
	}

	private static void kill(String pid) {
		try {
			Shell.processManager.killProcess(Integer.parseInt(pid));
		} catch (NumberFormatException e) {
			System.out.println("kill: pid must be a number");
		}
	}

	private static void ps() {
		System.out.println("id   state   name");
		System.out.println("--   -----   ----");

		for(Process p : Shell.processManager.getProcesses()) {
			int id = p.getPcb().getId();
			ProcessState state = p.getPcb().getState();
			String name = Programs.getName(p.getWords());
			System.out.println(id + "   " + state + "   " + name);
		}
	}

	private static void pdump(String pid) {
		try {
			Process process = Shell.processManager.getProcess(
				Integer.parseInt(pid)
			);

			System.out.println(process.getPcb());
			listFramesProcess(process.getPcb().getCpuState().getFrames());
		}catch (Exception e) {
			System.out.println("pdump: invalid pid");
		}
	}
	private static void listFramesProcess(int[] range){
		try {
			for (int r: range) {
				System.out.println("frame " + r);

				for (Word word: Shell.memory.getFrame(r)) {
					System.out.println("    " + word);
				}
			}
		} catch (NumberFormatException e) {
			System.out.println("pdump: invalid process range");
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("pdump: address out of bounds");
		}
	}

	private static void mdump(String[] input) {
		try {
			int start = Integer.parseInt(input[1]);
			int end = Integer.parseInt(input[2]);

			for (int i = start; i <= end; i++) {
				System.out.println("frame " + i);

				for (Word word: Shell.memory.getFrame(i)) {
					System.out.println("    " + word);
				}
			}
		} catch (NumberFormatException e) {
			System.out.println("mdump: invalid start and end addresses");
			System.out.println("Usage: mdump <start> <end>");
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("mdump: address out of bounds");
		}
	}

	private static void runAll() {
		Shell.processManager.runAll();
	}

	private static void trace() {
		Shell.cpu.toggleTrace();
	}
}

