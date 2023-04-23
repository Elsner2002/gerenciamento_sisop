import java.util.Scanner;

public class VirtualMachine {
	private static Cpu cpu;

	public static void main(String[] args) {
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
}

