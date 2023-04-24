import java.util.Scanner;

public class SyscallHandler {
	public void handle(CpuState cpuState) {
        if (cpuState.getReg(8) == 1) {
            int elementR9 = cpuState.getReg(9);
            Scanner in = new Scanner(System.in);
            int userInput = in.nextInt();
            cpuState.setReg(elementR9, userInput);
            in.close();
        } else if (cpuState.getReg(8) == 2) {
            int elementR9 = cpuState.getReg(9);
            int storedR9 = cpuState.getReg(elementR9);
            System.out.println(storedR9);
        }
	}
}
