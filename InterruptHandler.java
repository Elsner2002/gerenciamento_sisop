public class InterruptHandler {
    private ProcessManager pm;

	public void handle(CpuState cpuState) {
        switch(cpuState.getIrpt()) {
            case INVALID_ADDRESS:
                pm.killProcess();
                System.out.println("Error: INVALID_ADDRESS");
                break;
            case INVALID_INSTRUCTION:
                pm.killProcess();
                System.out.println("Error: INVALID_INSTRUCTION");
                break;
            case OVERFLOW:
                pm.killProcess();
                System.out.println("Error: OVERFLOW");
                break;
            case STOP:
                cpuState.stopProcess();
                break;
        }

		cpuState.setIrpt(null);
	}
}
