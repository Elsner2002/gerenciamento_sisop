public class ProcessManager {
	private int nextId = 0;
	private Memory memory;
	private boolean[] occupiedFrames;

	public void newProcess(Process process) {
		int totalFramesNeeded = (
			process.getWords().length / Memory.FRAME_SIZE
		) + 1;

		int framesNeeded = totalFramesNeeded;
		int[] pages = new int[framesNeeded];

		for (int i = 0; i < Memory.FRAME_AMOUNT; i++) {
			if (!occupiedFrames[i]) {
				pages[totalFramesNeeded - framesNeeded] = i;
				framesNeeded -= 1;
				occupiedFrames[i] = true;
			}

			if (framesNeeded == 0) {
				break;
			}
		}

		Pcb pcb = new Pcb(nextId, pages);
		nextId += 1;
		process.setPcb(pcb);
		allocateProcess(process);
	}

	private void allocateProcess(Process process) {
		int[] pages = process.getPcb().getPages();
		Word[] words = process.getWords();

		for (int i = 0; i < pages.length; i++) {
			Word[] frame = memory.getFrame(pages[i]);
			System.arraycopy(words, wordsPos, frame, 0, length);
		}
	}
}

