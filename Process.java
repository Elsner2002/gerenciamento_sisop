public class Process {
	private Pcb pcb;
	private Word[] words;

	public Process(Word[] words) {
		this.pcb = null;
		this.words = words;
	}

	public Pcb getPcb() {
		return pcb;
	}

	public void setPcb(Pcb pcb) {
		this.pcb = pcb;
	}

	public Word[] getWords() {
		return words;
	}
}
