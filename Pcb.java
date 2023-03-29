public class Pcb {
	private int id;
	private boolean running;
	private boolean ready;
	private CpuState cpuState;
	private int[] pages;

	public int[] getPages() {
		return pages;
	}
}

