public class Memory {
	public static final int MEMORY_SIZE = 1024;
	public static final int FRAME_SIZE = 8;
	public static final int FRAME_AMOUNT = Memory.MEMORY_SIZE / Memory.FRAME_SIZE;
	private Word[][] memory;

	public Memory() {
		this.memory = new Word[Memory.FRAME_AMOUNT][Memory.FRAME_SIZE];
	}

	public Word get(int addr) {
		int frame = addr / Memory.FRAME_SIZE;
		int frame_start = frame * Memory.FRAME_SIZE;
		int offset = addr - frame_start;
		return this.memory[frame][offset];
	}

	public void set(int addr, Word value) {
		int frame = addr / Memory.FRAME_SIZE;
		int frame_start = frame * Memory.FRAME_SIZE;
		int offset = addr - frame_start;
		this.memory[frame][offset] = value;
	}

	public void set(int addr, int param) {
		Word oldValue = this.get(addr);

		Word newValue = new Word(
			Opcode.DATA, oldValue.r1(), oldValue.r2(), param
		);

		this.set(addr, newValue);
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
