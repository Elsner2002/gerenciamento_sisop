import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class OperatingSystem {
	private static Cpu cpu;
	private static ProcessManager pm;
	//private static MemoryManager mm;
	private static List<Integer> idProcess = new ArrayList<Integer>();
	private static Map<String, Word[]> programs= new HashMap<>();
	private static Programs programList;

	public static void fillPrograms(){
		programs.put("PB", programList.PB);
		programs.put("PC", programList.PC);
		programs.put("Fatorial", programList.fatorial);
		programs.put("FatorialTrap", programList.fatorialTRAP);
		programs.put("Fibonacci10", programList.fibonacci10);
		programs.put("FibonacciTrap", programList.fibonacciTRAP);
		programs.put("ProgMinimo", programList.progMinimo);
	}

	public static String getNome(Word[] words){
		return programs.entrySet()
            	.stream()
            	.filter(e -> e.getValue().equals(words))
            	.findFirst()
            	.map(Map.Entry::getKey)
            	.orElse(null);
	}

	public static void main(String[] args) {
		fillPrograms();

		Scanner in = new Scanner(System.in);
		boolean execVM = true;
		String entrada = "";
		String[] vetEntr;
		
		do{
			entrada = in.next();
			vetEntr = entrada.split(" ");
			switch(vetEntr[0]){
				case "cria":
					idProcess.add(pm.createProcess(programs.get(vetEntr[1])));
					break;

				case "dump":
					System.out.print(pm.getProcess(Integer.parseInt(vetEntr[1])).getPcb().toString());
					break;

				case "listaProcessos":
				System.out.println("ID   State    Name");
				System.out.println("--   -----    ----");
					for(int id : idProcess){
						System.out.println(id+"    "+pm.getProcess(id).getPcb().getState()+"  "+getNome(pm.getProcess(id).getWords()));
					}
					break;

				case "desaloca":
					pm.killProcess(Integer.parseInt(vetEntr[1]));
					break;

				case "dumpM":
					break;

				case "executa":
					cpu.run();
					break;

				case "traceOn":
					cpu.traceOn();
					break;

				case "traceOff":
					cpu.traceOff();
					break;
					
				case "exit":
					execVM = false;
					break;

				default:
					break;
			}

		} while(execVM);
	
		in.close();
	}
}

