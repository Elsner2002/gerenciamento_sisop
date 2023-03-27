public class Memory {
	public static final int MEMORY_SIZE;
	public static final int FRAME_SIZE;
	public static final int FRAME_AMOUNT = Memory.MEMORY_SIZE / Memory.FRAME_SIZE;
	private Word[][] memory;
	private int[] framesPerProcess;

	public Memory() {
		this.memory = new Word[Memory.FRAME_AMOUNT][Memory.FRAME_SIZE];
		this.framesPerProcess = new int[FRAME_AMOUNT];
	}

	public int getPosition(Process p, int pc) {
		int page = pc / FRAME_SIZE;
		int offset = pc % FRAME_SIZE;
		return p.getPage(pc) * FRAME_SIZE + offset;
	}
}

