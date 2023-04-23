public class InterruptHandler {
	public void handle(CpuState cpuState) {
        switch(cpuState.getIrpt()){
            case INVALID_ADDRESS:
                break;
            
            case INVALID_INSTRUCTION:
                break;
            
            case OVERFLOW:
                break;

            case STOP:
                cpuState.stopProcess();
                break;
        }
		cpuState.setIrpt(null);
	}
}