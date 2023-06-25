public class Process {
	private Pcb pcb;
	private Word[] words;
	//cria os processos com seus conteudo e PCB
	public Process(Pcb pcb, Word[] words) {
		this.pcb = pcb;
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
