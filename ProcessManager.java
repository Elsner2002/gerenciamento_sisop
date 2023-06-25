import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;

public class ProcessManager {
	private int nextId = 0;
	private int runningId = -1;
	private Cpu cpu;
	private MemoryManager memoryManager;
	private Map<Integer, Process> processes;
	private Queue<Integer> idQueue;
	private Queue<Integer> blockedIdQueue;
	private NextCpuState nextCpuState;

	public ProcessManager(
		MemoryManager memoryManager, NextCpuState nextCpuState
	) {
		this.memoryManager = memoryManager;
		this.processes = new HashMap<>();
		this.idQueue = new LinkedList<>();
		this.blockedIdQueue = new LinkedList<>();
		this.nextCpuState = nextCpuState;
	}

	public void runAll() {
		this.runningId = this.idQueue.remove();
		this.idQueue.add(this.runningId);
		this.run(this.runningId);
	}

	public void run(int id) {
		if (this.cpu == null) {
			throw new IllegalStateException("No reference to CPU");
		}

		Process next = this.processes.get(id);
		next.getPcb().setState(ProcessState.RUNNING);
		this.runningId = id;
		this.nextCpuState.set(next.getPcb().getCpuState());
	}

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

	public void timeout() {
		Process runningProcess = this.processes.get(this.runningId);
		runningProcess.getPcb().setState(ProcessState.READY);
		runningProcess.getPcb().setCpuState(this.cpu.getCpuState());
		reschedule();
	}

	public void block() {
		Process runningProcess = this.processes.get(this.runningId);
		runningProcess.getPcb().setState(ProcessState.BLOCKED);
		runningProcess.getPcb().setCpuState(this.cpu.getCpuState());
		this.blockedIdQueue.add(this.runningId);
		reschedule();
	}

	public void unblock() {
		try {
			int blockedId = this.blockedIdQueue.remove();

			this.processes.get(blockedId).getPcb().setState(
				ProcessState.READY
			);

			if (this.runningId == -1) {
				this.reschedule();
			}
		} catch (NoSuchElementException e) {
			return;
		}
	}

	private void reschedule() {
		int nextIdToRun = -1;

		while (true) {
			try {
				nextIdToRun = this.idQueue.remove();
			} catch (NoSuchElementException e) {
				return;
			}

			this.idQueue.add(nextIdToRun);
			Process nextProcess = this.processes.get(nextIdToRun);
			ProcessState nextProcessState = nextProcess.getPcb().getState();

			if (
				nextIdToRun == this.runningId &&
				nextProcessState == ProcessState.BLOCKED
			) {
				this.runningId = -1;
				return;
			}

			if (nextProcessState == ProcessState.READY) {
				break;
			}
		}

		this.run(nextIdToRun);
	}

	public void killRunning() {
		this.killProcess(this.runningId);

		try {
			this.runningId = this.idQueue.peek();
		} catch (NullPointerException e) {
			this.runningId = -1;
			return;
		}

		this.reschedule();
	}

	public void killProcess(int pid) {
		if (pid == this.runningId) {
			this.runningId = -1;
		}

		Process process = this.processes.get(pid);

		this.memoryManager.desallocate(
			process.getPcb().getCpuState().getFrames()
		);

		this.idQueue.remove(pid);
		this.processes.remove(pid);
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

	public Process getProcess(int id){
		return processes.get(id);
	}

	public Process[] getProcesses() {
		return this.processes.values().toArray(new Process[
			this.processes.values().size()
		]);
	}
}
