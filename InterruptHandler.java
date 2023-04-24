public class InterruptHandler {
    private ProcessManager pm;
    private MemoryManager mm;

    public InterruptHandler(Memory memory){
        mm = new MemoryManager(memory);
		pm = new ProcessManager(mm);
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
                pm.killRunning();
                System.out.println("Error: INVALID_ADDRESS");
                break;
            case INVALID_INSTRUCTION:
                pm.killRunning();
                System.out.println("Error: INVALID_INSTRUCTION");
                break;
            case OVERFLOW:
                pm.killRunning();
                System.out.println("Error: OVERFLOW");
                break;
            case STOP:
                cpuState.stopProcess();
                break;
        }

		cpuState.setIrpt(null);
	}
}
