import Word;

public class Memory {
	public static final int MEMORY_SIZE = 1024;
	public static final int FRAME_SIZE = 8;
	public static final int FRAME_AMOUNT = Memory.MEMORY_SIZE / Memory.FRAME_SIZE;
	private Word[][] memory;

	public Memory() {
		this.memory = new Word[Memory.FRAME_AMOUNT][Memory.FRAME_SIZE];
	}

	public int getPosition(Process p) {
		int page = p.getPcb().getPc() / FRAME_SIZE;
		int offset = p.getPcb().getPc() % FRAME_SIZE;
		return p.getPage(pc) * FRAME_SIZE + offset;
	}

	public Word[][] getMemory() {
		return this.memory;
	}

	public Word[] getFrame(int i) {
		return this.memory[i];
	}

	public void setFrame(int i, Word[] words) {
		this.memory[i] = words;
	}
}
