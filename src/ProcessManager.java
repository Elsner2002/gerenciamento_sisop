import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * Manages process lifetime and scheduling.
 */
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

	/**
	 * Prompts the CPU to run a process.
	 *
	 * @param id the process id
	 */
	public void run(int id) {
		if (this.cpu == null) {
			throw new IllegalStateException("No reference to CPU");
		}

		Process next = this.processes.get(id);
		next.getPcb().setState(ProcessState.RUNNING);
		this.runningId = id;
		this.nextCpuState.set(next.getPcb().getCpuState());
	}

	/**
	 * Allocate a new process in memory.
	 *
	 * @param words the process data
	 * @return the process id, or -1 if it could not be created.
	 */
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

		if (this.runningId == -1) {
			this.run(id);
		}

		return id;
	}

	/**
	 * Reschedule process due to the end of current round-robin quantum.
	 */
	public void timeout() {
		if (this.runningId != -1) {
			Process runningProcess = this.processes.get(this.runningId);
			runningProcess.getPcb().setState(ProcessState.READY);
			runningProcess.getPcb().setCpuState(this.cpu.getCpuState());
		}

		reschedule();
	}

	/**
	 * Blocks the current process and reschedule.
	 */
	public void block() {
		Process runningProcess = this.processes.get(this.runningId);
		runningProcess.getPcb().setState(ProcessState.BLOCKED);
		runningProcess.getPcb().setCpuState(this.cpu.getCpuState());
		this.blockedIdQueue.add(this.runningId);
		reschedule();
	}

	/**
	 * Unblocks the process which is blocked for the longest time.
	 */
	public void unblock() {
		try {
			int blockedId = this.blockedIdQueue.remove();

			this.processes.get(blockedId).getPcb().setState(
				ProcessState.READY
			);

			// If no process was running, reschedule (which will result in
			// this process being run).
			if (this.runningId == -1) {
				this.reschedule();
			}
		} catch (NoSuchElementException e) {
			return;
		}
	}

	/**
	 * Choose which process will be the next to run.
	 */
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

			// If looped through the entire queue (returned to the running
			// process) and the running process is blocked, then no process
			// can be run.
			if (
				nextIdToRun == this.runningId &&
				nextProcessState == ProcessState.BLOCKED
			) {
				this.runningId = -1;
				return;
			}

			// Run this process.
			if (nextProcessState == ProcessState.READY) {
				break;
			}
		}

		this.run(nextIdToRun);
	}

	/**
	 * Kill the running process and reschedule,
	 * unless there are no more processes.
	 */
	public void killRunning() {
		this.killProcess(this.runningId);

		// If there are more processes, mark one of them as running.
		// This does not run the process, and is done only to identify
		// the start of the queue when later rescheduling.
		try {
			this.runningId = this.idQueue.peek();
		} catch (NullPointerException e) {
			this.runningId = -1;
			return;
		}

		this.reschedule();
	}

	/**
	 * Remove a process from memory.
	 *
	 * @param pid the process id
	 */
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
