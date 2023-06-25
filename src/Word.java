/**
 * Natural unit of data for this CPU architecture.
 */
public record Word(
	Opcode opcode,
	int r1,
	int r2,
	int param
) {
	@Override
	public String toString() {
		return this.opcode() + " " + this.r1() + " " + this.r2() + " " + this.param();
	}
}
