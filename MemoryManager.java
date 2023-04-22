public class MemoryManager {
	private Memory memory;
	private int numOccupiedFrames = 0;
	private boolean[] occupiedFrames;

	public boolean fillFrames(int[] frames, Word[] words) {
		if (words.length < frames.length * Memory.FRAME_SIZE) {
			return false;
		}

		for (int i : frames) {
			if (!occupiedFrames[i]) {
				return false;
			}
		}

		for (int remaining = words.length; remaining > 0; remaining--) {
			Word[] slice = new Word[Memory.FRAME_SIZE];

			System.arraycopy(
				words, (words.length - remaining) * Memory.FRAME_SIZE,
				slice, 0, Memory.FRAME_SIZE
			);

			this.memory.setFrame(frames[i], slice);
		}

		return true;
	}

	public int[] allocate(int framesNeeded) {
		if (this.numOccupiedFrames < framesNeeded) {
			return null;
		}

		int framesRemaining = framesNeeded;
		int[] allocatedFrames = new int[framesNeeded];

		for (int i = 0; i < Memory.FRAME_AMOUNT; i++) {
			if (!this.occupiedFrames[i]) {
				allocatedFrames[framesNeeded - framesRemaining] = i;
				framesRemaining -= 1;
				this.occupiedFrames[i] = true;
				this.numOccupiedFrames += 1;
			}

			if (framesRemaining == 0) {
				break;
			}
		}

		return allocatedFrames;
	}

	public void desallocate(int[] frames) {
		for (int frame : frames) {
			this.occupiedFrames[frame] = false;
			this.numOccupiedFrames -= 1;
		}
	}
}
