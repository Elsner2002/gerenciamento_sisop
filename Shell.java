import java.util.Scanner;

public class Shell {
	private static Cpu cpu;
	private static Memory memory;
	private static ProcessManager processManager;
	private static ShellIO shellIO;

	public static void main(String[] args) {
		Shell.memory = new Memory();
		MemoryManager memoryManager = new MemoryManager(Shell.memory);
		NextCpuState nextCpuState = new NextCpuState();
		SyscallQueue syscallQueue = new SyscallQueue();
		Shell.shellIO = new ShellIO();

		Shell.processManager = new ProcessManager(
			memoryManager, nextCpuState
		);

		InterruptHandler interruptHandler = new InterruptHandler(
			Shell.processManager
		);

		SyscallHandler syscallHandler = new SyscallHandler(
			Shell.memory, interruptHandler, syscallQueue, Shell.shellIO
		);

		Shell.cpu = new Cpu(
			Shell.memory, interruptHandler, nextCpuState, syscallQueue
		);

		Shell.processManager.setCpu(Shell.cpu);
		Shell.cpu.start();
		syscallHandler.start();
		prompt();
	}

	private static void prompt() {
		while (true) {
			String[] input = Shell.shellIO.getInputLine("[dotti@pucrs]& ")
				.split(" ");

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
					return;
				case "":
					break;
				default:
					Shell.shellIO.println(
						"system: command not found: " + input[0]
					);

					break;
			}
		}
	}

	private static void new_(String name) {
		try {
			Shell.processManager.createProcess(Programs.get(name));
		} catch (Exception e) {
			Shell.shellIO.println("new: unknown program");
		}
	}

	private static void kill(String pid) {
		try {
			Shell.processManager.killProcess(Integer.parseInt(pid));
		} catch (NumberFormatException e) {
			Shell.shellIO.println("kill: pid must be a number");
		}
	}

	private static void ps() {
		Shell.shellIO.println(
			"id   state   name\n" +
			"--   -----   ----"
		);

		for(Process p : Shell.processManager.getProcesses()) {
			int id = p.getPcb().getId();
			ProcessState state = p.getPcb().getState();
			String name = Programs.getName(p.getWords());
			Shell.shellIO.println(id + "   " + state + "   " + name);
		}
	}

	private static void pdump(String pid) {
		try {
			Process process = Shell.processManager.getProcess(
				Integer.parseInt(pid)
			);

			Shell.shellIO.println(process.getPcb().toString());
			listFramesProcess(process.getPcb().getCpuState().getFrames());
		}catch (Exception e) {
			Shell.shellIO.println("pdump: invalid pid");
		}
	}
	private static void listFramesProcess(int[] range){
		try {
			for (int r: range) {
				Shell.shellIO.println("frame " + r);

				for (Word word: Shell.memory.getFrame(r)) {
					Shell.shellIO.println("    " + word);
				}
			}
		} catch (NumberFormatException e) {
			Shell.shellIO.println("pdump: invalid process range");
		} catch (ArrayIndexOutOfBoundsException e) {
			Shell.shellIO.println("pdump: address out of bounds");
		}
	}

	private static void mdump(String[] input) {
		try {
			int start = Integer.parseInt(input[1]);
			int end = Integer.parseInt(input[2]);

			for (int i = start; i <= end; i++) {
				Shell.shellIO.println("frame " + i);

				for (Word word: Shell.memory.getFrame(i)) {
					Shell.shellIO.println("    " + word);
				}
			}
		} catch (NumberFormatException e) {
			Shell.shellIO.println("mdump: invalid start and end addresses");
			Shell.shellIO.println("Usage: mdump <start> <end>");
		} catch (ArrayIndexOutOfBoundsException e) {
			Shell.shellIO.println("mdump: address out of bounds");
		}
	}

	private static void runAll() {
		Shell.processManager.runAll();
	}

	private static void trace() {
		Shell.cpu.toggleTrace();
	}
}

