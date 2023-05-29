import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;

//cria o gerenciador de processos
public class ProcessManager {
	private int nextId = 0;
	private int runningId = -1;
	private MemoryManager memoryManager;
	private Map<Integer, Process> processes;
	private Queue<Integer> idQueue;

	public ProcessManager(MemoryManager memoryManager) {
		this.memoryManager = memoryManager;
		this.processes = new HashMap<>();
		this.idQueue = new PriorityQueue<>();
	}

	public Map<Integer, Process> getMap(){
		return processes;
	}
	//roda o processo
	public void run(int id) {
		this.processes.get(id).getPcb().setState(ProcessState.RUNNING);
		this.runningId = id;
	}
	//pega o processo
	public Process getProcess(int id){
		return processes.get(id);
	}

	public Process[] getProcesses() {
		return this.processes.values().toArray(new Process[
			this.processes.values().size()
		]);
	}
	//cria o processo
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
		int id = pcb.getId();
		this.processes.put(id, process);
		this.idQueue.add(id);
		return id;
	}

	public void reschedule() {
		int nextIdToRun = -1;

		while (true) {
			try {
				nextIdToRun = this.idQueue.remove();
			} catch (NoSuchElementException e) {
				return;
			}

			Process nextP = this.processes.get(nextIdToRun);

			if (nextP != null) {
				continue;
			}

			if (nextP.getPcb().getState() != ProcessState.READY) {
				this.idQueue.add(nextIdToRun);
				continue;
			}

			break;
		}

		this.run(nextIdToRun);
	}

	//cria o processo rodando
	public void killRunning() {
		this.killProcess(this.runningId);
	}

	//desaloca o processo pelo id
	public void killProcess(int pid) {
		if (pid == this.runningId) {
			this.runningId = -1;
		}

		Process process = this.processes.get(pid);
		this.memoryManager.desallocate(process.getPcb().getFrames());
		this.processes.remove(pid);
	}
}
