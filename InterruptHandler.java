public class InterruptHandler {
    private ProcessManager processManager;

    public InterruptHandler(ProcessManager processManager){
        this.processManager = processManager;
    }

	public void handle(CpuState cpuState) {

        switch(cpuState.getIrpt()) {
            case INVALID_ADDRESS:
            /*System.out.println(pm.getMap().values());
                for(Process i: pm.getMap().values()){
                    System.out.println("entra");
                    System.out.println(i.toString());
                }
            */
                processManager.killRunning();
                System.out.println("Error: INVALID_ADDRESS");
                break;
            case INVALID_INSTRUCTION:
                processManager.killRunning();
                System.out.println("Error: INVALID_INSTRUCTION");
                break;
            case OVERFLOW:
                processManager.killRunning();
                System.out.println("Error: OVERFLOW");
                break;
            case STOP:
                cpuState.stopProcess();
                break;
        }

		cpuState.setIrpt(null);
	}
}
