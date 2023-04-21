public class Pcb {
	private int id;
	private boolean running;
	private boolean ready;
	private CpuState cpuState;
	private int[] pages;

	public Pcb(int id, int[] pages) {
		this.id = id;
		this.pages = pages;
	}

	public int[] getPages() {
		return pages;
	}
}

