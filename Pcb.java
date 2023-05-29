public class Pcb {
	private int id;
	private int pc;
	private ProcessState state;
	private CpuState cpuState;
	private int[] frames;
	//instancia o PCB de um programa
	public Pcb(int id, int[] frames) {
		this.id = id;
		this.frames = frames;
		this.state = ProcessState.READY;
		this.cpuState = null;
	}

	public int getId() {
		return this.id;
	}

	public int getPc() {
		return this.pc;
	}

	public int[] getFrames() {
		return this.frames;
	}

	public ProcessState getState() {
		return this.state;
	}

	public void setState(ProcessState state) {
		this.state = state;
	}

	public CpuState getCpuState() {
		return this.cpuState;
	}

	public void setCpuState(CpuState cpuState) {
		this.cpuState = cpuState;
	}

	public String toString(){
		String content = "id: " + id + "\npc: " + pc + "\nstate: " + getState()
			+ "\nframes: ";

		for(int frame: frames) {
			content += frame + " ";
		}

		return content;
	}
}
