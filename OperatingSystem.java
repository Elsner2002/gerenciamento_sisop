import java.util.Scanner;

public class OperatingSystem {
	private static Cpu cpu;
	private static Memory memory;
	private static ProcessManager processManager;
	//inicia o programa
	public static void main(String[] args) {
		Memory memory = new Memory();
		MemoryManager memoryManager = new MemoryManager(memory);
		ProcessManager processManager = new ProcessManager(memoryManager);
		InterruptHandler interruptHandler = new InterruptHandler(processManager);
		SyscallHandler syscallHandler = new SyscallHandler(memory);
		Cpu cpu = new Cpu(memory, interruptHandler, syscallHandler);

		OperatingSystem.cpu = cpu;
		OperatingSystem.memory = memory;
		OperatingSystem.processManager = processManager;

		run();
	}
	//executa as operações do SO
	private static void run() {
		Scanner in = new Scanner(System.in);

		while (true) {
			System.out.print("[dotti@pucrs]& ");
			String[] input = in.nextLine().split(" ");

			switch(input[0]) {
				case "new":
					OperatingSystem.new_(input[1]);
					break;
				case "kill":
					OperatingSystem.kill(input[1]);
					break;
				case "ps":
					OperatingSystem.ps();
					break;
				case "pdump":
					OperatingSystem.pdump(input[1]);
					break;
				case "mdump":
					OperatingSystem.mdump(input);
					break;
				case "run":
					OperatingSystem.run(input[1]);
					break;
				case "trace":
					OperatingSystem.trace();
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
	//cria novo processo
	private static void new_(String name) {
		try {
			OperatingSystem.processManager.createProcess(
				Programs.get(name)
			);
		} catch (Exception e) {
			System.out.println("new: unknown program");
		}
	}
	//desaloca processo existente pelo id
	private static void kill(String pid) {
		try {
			OperatingSystem.processManager.killProcess(Integer.parseInt(pid));
		} catch (NumberFormatException e) {
			System.out.println("kill: pid must be a number");
		}
	}
	//lista processos criados
	private static void ps() {
		System.out.println("id   state   name");
		System.out.println("--   -----   ----");

		for(Process p : OperatingSystem.processManager.getProcesses()) {
			int id = p.getPcb().getId();
			ProcessState state = p.getPcb().getState();
			String name = Programs.getName(p.getWords());
			System.out.println(id + "   " + state + "   " + name);
		}
	}
	//mostra informações do processo pelo id (tanto pcb quanto os frames que está)
	private static void pdump(String pid) {
		try {
			Process process = OperatingSystem.processManager.getProcess(
				Integer.parseInt(pid)
			);

			System.out.println(process.getPcb());
			listFramesProcess(process.getPcb().getFrames());
		}catch (Exception e) {
			System.out.println("pdump: invalid pid");
		}
	}
	private static void listFramesProcess(int[] range){
		try {
			for (int r: range) {
				System.out.println("frame " + r);

				for (Word word: OperatingSystem.memory.getFrame(r)) {
					System.out.println("    " + word);
				}
			}
		} catch (NumberFormatException e) {
			System.out.println("pdump: invalid process range");
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("pdump: address out of bounds");
		}
	}
	//mostra o que está alocado na memória no intervalo passado
	private static void mdump(String[] input) {
		try {
			int start = Integer.parseInt(input[1]);
			int end = Integer.parseInt(input[2]);

			for (int i = start; i <= end; i++) {
				System.out.println("frame " + i);

				for (Word word: OperatingSystem.memory.getFrame(i)) {
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
	//executa processo pelo seu id
	private static void run(String pid) {
		try {
			int numPid = Integer.parseInt(pid);
			OperatingSystem.processManager.run(numPid);

			Process process = OperatingSystem.processManager
				.getProcess(numPid);

			OperatingSystem.cpu.run(process);
		} catch (Exception e) {
		 	System.out.println("run: invalid pid");
		}
	}
	//liga/desliga o trace da execução pela CPU
	private static void trace() {
		OperatingSystem.cpu.toggleTrace();
	}
}

