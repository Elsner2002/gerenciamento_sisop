/**
 * Process Control Block.
 */
public class Pcb {
	private int id;
	private ProcessState state;
	private CpuState cpuState;

	public Pcb(int id, int[] frames) {
		this.id = id;
		this.state = ProcessState.READY;
		this.cpuState = new CpuState(frames);
	}

	public int getId() {
		return this.id;
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
		String content =
			"id: " + id
			+ "\nstate: " + getState()
			+ "\nframes: ";

		for(int frame: this.getCpuState().getFrames()) {
			content += frame + " ";
		}

		return content;
	}
}
