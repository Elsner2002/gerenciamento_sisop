public record Word(
	//cria uma palavra e guarda as suas informações
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
