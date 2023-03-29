import java.util.Scanner;

public class VirtualMachine {
	private static Cpu cpu;

	public static void main(String[] args) {
		Scanner in = new Scanner(system.in);
		boolean execVM = true;
		do{
			String entrada = in.next();
			String[] vetEntr = entrada.split(" ");
			switch(vetEntr[0]){
				case "cria":
					break;

				case "dump":
					break;

				case "listaProcessos":
					break;

				case "desaloca":
					break;

				case "dumpM":
					break;

				case "executa":
					break;

				case "traceOn":
					break;

				case "traceOff":
					break;
					
				case "exit":
					execVM = false;
					break;

				default:
					break;
			}

		} while(execVM)
	}
}

