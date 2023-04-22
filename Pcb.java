public class Pcb {
	private int id;
	private int pc;
	private boolean running;
	private boolean ready;
	private CpuState cpuState;
	private int[] frames;

	public Pcb(int id, int[] frames) {
		this.id = id;
		this.frames = frames;
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
}
