public class Memory {
	public static final int MEMORY_SIZE = 1024;
	public static final int FRAME_SIZE = 8;
	public static final int FRAME_AMOUNT = Memory.MEMORY_SIZE / Memory.FRAME_SIZE;
	private Word[][] memory;

	public Memory() {
		this.memory = new Word[Memory.FRAME_AMOUNT][Memory.FRAME_SIZE];
	}

	public int getPosition(int[] pages, int virtual_addr) {
		int page = virtual_addr / Memory.FRAME_SIZE;
		int page_start = page * Memory.FRAME_SIZE;
		int frame = pages[page];
		int frame_start = (frame - 1) * Memory.FRAME_SIZE;
		int offset = virtual_addr - page_start;
		return frame_start + offset;
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
