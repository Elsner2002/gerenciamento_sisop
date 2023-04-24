public class MemoryManager {
	private Memory memory;
	private int numOccupiedFrames;
	private boolean[] occupiedFrames;
	//cria o gerenciador de memória
	public MemoryManager(Memory memory) {
		this.memory = memory;
		this.numOccupiedFrames = 0;
		this.occupiedFrames = new boolean[Memory.FRAME_AMOUNT];
	}

	public int getNumOccupiedFrames() {
		return numOccupiedFrames;
	}
	//coloca o conteudo do processo na memoria
	public boolean fillFrames(int[] frames, Word[] words) {
		if (words.length > frames.length * Memory.FRAME_SIZE) {
			return false;
		}

		for (int i : frames) {
			if (!occupiedFrames[i]) {
				return false;
			}
		}

		int framesNeeded = words.length/Memory.FRAME_SIZE;

		if (words.length % Memory.FRAME_SIZE != 0 ){
			framesNeeded++;
		}

		for (int framesLeft = framesNeeded; framesLeft > 0; framesLeft--) {
			int frameIndex = framesNeeded - framesLeft;
			Word[] slice = new Word[Memory.FRAME_SIZE];
			int length = Memory.FRAME_SIZE;

			if (framesLeft == 1) {
				length = words.length % Memory.FRAME_SIZE;
			}

			System.arraycopy(
				words, frameIndex * Memory.FRAME_SIZE,
				slice, 0, length
			);

			this.memory.setFrame(frames[frameIndex], slice);
		}

		return true;
	}

	//aloca espaço na memória
	public int[] allocate(int framesNeeded) {
		if (
			framesNeeded > Memory.FRAME_AMOUNT - this.getNumOccupiedFrames()
		) {
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
	//desaloca espaço na memória
	public void desallocate(int[] frames) {
		for (int frame : frames) {
			this.occupiedFrames[frame] = false;
			this.numOccupiedFrames -= 1;
		}
	}
}
