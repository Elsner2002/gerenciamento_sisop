import java.util.Scanner;

public class OperatingSystem {
	private static Cpu cpu;
	private static Memory memory;
	private static ProcessManager processManager;

	public static void main(String[] args) {
		OperatingSystem.memory = new Memory();
		OperatingSystem.cpu = new Cpu();
		run();
	}

	private static void run() {
		Scanner in = new Scanner(System.in);

		while (true) {
			System.out.print("[user@host]& ");
			String[] input = in.nextLine().split(" ");

			switch(input[0]) {
				case "new":
					OperatingSystem.new_(input[0]);
					break;
				case "kill":
					OperatingSystem.kill(input[0]);
					break;
				case "ps":
					OperatingSystem.ps();
					break;
				case "pdump":
					OperatingSystem.pdump(input[0]);
					break;
				case "mdump":
					OperatingSystem.mdump(input);
					break;
				case "run":
					OperatingSystem.run(input[0]);
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

	private static void new_(String name) {
		try {
			OperatingSystem.processManager.createProcess(
				Programs.get(name)
			);
		} catch (Exception e) {
			System.out.println("new: unknown program");
		}
	}

	private static void kill(String pid) {
		try {
			OperatingSystem.processManager.killProcess(Integer.parseInt(pid));
		} catch (NumberFormatException e) {
			System.out.println("kill: pid must be a number");
		}
	}

	private static void ps() {
		System.out.println("id   state   name");
		System.out.println("--   -----   ----");

		for(int id : idProcess) {
			String state = OperatingSystem.processManager.getProcess(id)
				.getPcb().getState();

			String name = Programs.getName(
				pm.getProcess(id).getWords()
			);

			System.out.println(id + "   " + state + "   " + name);
		}
	}

	// TODO: Print pages.
	private static void pdump(String pid) {
		try {
			Process process = OperatingSystem.processManager.getProcess(
				Integer.parseInt(pid)
			);

			System.out.println(process.getPcb());
		} catch (Exception e) {
			System.out.println("pdump: invalid pid");
		}
	}

	private static void mdump(String[] input) {
		try {
			int start = Integer.parseInt(input[1]);
			int end = Integer.parseInt(input[2]);

			for (int i = start; i < end; i++) {
				System.out.println("frame " + i);

				for (Word word: memory.getFrame(i)) {
					System.out.println(word);
				}
			}
		} catch (NumberFormatException e) {
			System.out.println("mdump: invalid start and end addresses");
			System.out.println("Usage: mdump <start> <end>");
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("mdump: address out of bounds");
		}
	}

	private static void run(String pid) {
		try {
			OperatingSystem.processManager.run(Integer.parseInt(pid));
			OperatingSystem.cpu.run();
		} catch (Exception e) {
			System.out.println("run: invalid pid");
		}
	}

	private static void trace() {
		OperatingSystem.cpu.toggleTrace();
	}
}
