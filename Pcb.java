public class Pcb {
	private int id;
	private int pc;
	private boolean running;
	private boolean ready;
	private CpuState cpuState;
	private int[] frames;

	public Pcb(int id, int[] frames) {
		this.id = id;
		this.frames = frames;
	}

	public int getId() {
		return this.id;
	}

	public int getPc() {
		return this.pc;
	}

	public int[] getFrames() {
		return this.frames;
	}

	public String getState(){
		if(running){
			return "Running";
		}
		else if(ready){
			return "Ready";
		}
		else{
			return "Blocked";
		}
	}

	public void changeRunning(){
		running = !running;
	}

	public void changeReady(){
		ready = !ready;
	}

	public String toString(){
		String content = "ID: "+ id + "\nPC: " + pc+ "\nState: " + getState()+ "\nFrames: ";
		for(int frame: frames){
			content+= frame + "\n";
		}
		return content;
	}
}
