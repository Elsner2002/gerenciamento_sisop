public record CpuState() {
	int pc;
	Word ir;
	int[] registers;
	Interrupt irpt;
	Memory memory;
	boolean debugMode;
}
