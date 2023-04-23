import java.util.Scanner;

public class VirtualMachine {
	private static Cpu cpu;
	private static Memory memory;

	public static void main(String[] args) {
		VirtualMachine.memory = new Memory();
		VirtualMachine.cpu = new Cpu();
		run();
	}

	private static void run() {
		Scanner in = new Scanner(System.in);

		while (true) {
			System.out.print("[user@host]& ");
			String[] input = in.nextLine().split(" ");

			switch(input[0]) {
				case "new":
					break;
				case "kill":
					break;
				case "ps":
					break;
				case "pdump":
					break;
				case "mdump":
					VirtualMachine.mdump(input);
					break;
				case "run":
					break;
				case "trace":
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
}

