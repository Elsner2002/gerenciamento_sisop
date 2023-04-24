import java.util.Map;
import java.util.HashMap;

public class ProcessManager {
	private int nextId = 0;
	private MemoryManager memoryManager;
	private Map<Integer, Process> processes;

	public ProcessManager(MemoryManager memoryManager) {
		this.memoryManager = memoryManager;
		this.processes = new HashMap<>();
	}

	// TODO: Improve
	public void run(int id) {
		this.processes.get(id).getPcb().changeRunning();
	}

	public Process getProcess(int id){
		return processes.get(id);
	}

	public Process[] getProcesses() {
		return this.processes.values().toArray(new Process[
			this.processes.values().size()
		]);
	}

	public int createProcess(Word[] words) {
		int framesNeeded = (words.length / Memory.FRAME_SIZE) + 1;
		int[] frames = this.memoryManager.allocate(framesNeeded);

		if (frames == null) {
			return -1;
		}

		Pcb pcb = new Pcb(this.nextId, frames);
		this.nextId += 1;
		boolean filled = this.memoryManager.fillFrames(frames, words);

		if (!filled) {
			return -1;
		}

		Process process = new Process(pcb, words);
		this.processes.put(pcb.getId(), process);
		return process.getPcb().getId();
	}

	public void killProcess(int pid) {
		Process process = this.processes.get(pid);
		this.memoryManager.desallocate(process.getPcb().getFrames());
		process.getPcb().changeRunning();
		this.processes.remove(pid);
	}

	public void killProcess() {
		for (int p: processes.keySet()){
			if(processes.get(p).getPcb().isRunning()){
				Process process = this.processes.get(p);
				this.memoryManager.desallocate(process.getPcb().getFrames());
				process.getPcb().changeRunning();
				this.processes.remove(p);
				break;
			}
		}
	}
}
