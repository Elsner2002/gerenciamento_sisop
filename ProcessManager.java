import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;

//cria o gerenciador de processos
public class ProcessManager {
	private int nextId = 0;
	private int runningId = -1;
	private Cpu cpu;
	private MemoryManager memoryManager;
	private Map<Integer, Process> processes;
	private Queue<Integer> idQueue;

	public ProcessManager(MemoryManager memoryManager) {
		this.memoryManager = memoryManager;
		this.processes = new HashMap<>();
		this.idQueue = new LinkedList<>();
	}

	public void setCpu(Cpu cpu) {
		if (this.cpu != null) {
			throw new IllegalStateException("CPU is already defined");
		}

		this.cpu = cpu;
	}

	public Map<Integer, Process> getMap(){
		return processes;
	}

	public void runAll() {
		this.runningId = this.idQueue.remove();
		this.idQueue.add(this.runningId);
		this.run(this.runningId);
	}

	//roda o processo
	public void run(int id) {
		if (this.cpu == null) {
			throw new IllegalStateException("No reference to CPU");
		}

		Process next = this.processes.get(id);
		next.getPcb().setState(ProcessState.RUNNING);
		this.runningId = id;
		this.cpu.run(next.getPcb().getCpuState());
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

		boolean filled = this.memoryManager.fillFrames(frames, words);

		if (!filled) {
			return -1;
		}

		Pcb pcb = new Pcb(this.nextId++, frames);
		Process process = new Process(pcb, words);
		int id = pcb.getId();
		this.processes.put(id, process);
		this.idQueue.add(id);
		return id;
	}

	public void reschedule() {
		Process runningProcess = this.processes.get(this.runningId);

		if (runningProcess != null) {
			runningProcess.getPcb().setState(ProcessState.READY);
			runningProcess.getPcb().setCpuState(this.cpu.getState());
		}

		int nextIdToRun = -1;

		while (true) {
			try {
				nextIdToRun = this.idQueue.remove();
			} catch (NoSuchElementException e) {
				return;
			}

			Process nextP = this.processes.get(nextIdToRun);

			if (nextP == null) {
				continue;
			}

			this.idQueue.add(nextIdToRun);

			if (nextP.getPcb().getState() == ProcessState.READY) {
				break;
			}
		}

		this.run(nextIdToRun);
	}

	//cria o processo rodando
	public void killRunning() {
		this.killProcess(this.runningId);
		this.reschedule();
	}

	//desaloca o processo pelo id
	public void killProcess(int pid) {
		if (pid == this.runningId) {
			this.runningId = -1;
		}

		Process process = this.processes.get(pid);

		this.memoryManager.desallocate(
			process.getPcb().getCpuState().getFrames()
		);

		this.processes.remove(pid);
	}
}
