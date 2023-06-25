import java.util.Map;

/**
 * Programs in disk.
 */
class Programs {
	public static final Word[] get(String name) {
		return Programs.programs.get(name);
	}

	public static String getName(Word[] words){
		return Programs.programs.entrySet()
			.stream()
			.filter(e -> e.getValue().equals(words))
			.findFirst()
			.map(Map.Entry::getKey)
			.orElse(null);
	}

	static Word[] factorial = new Word[] {
		// este fatorial so aceita valores positivos. nao pode ser zero
		new Word(Opcode.LDI, 0, -1, 4), // 0 r0 e valor a calcular fatorial
		new Word(Opcode.LDI, 1, -1, 1), // 1 r1 e 1 para multiplicar (por r0)
		new Word(Opcode.LDI, 6, -1, 1), // 2 r6 e 1 para ser o decremento
		new Word(Opcode.LDI, 7, -1, 8), // 3 r7 tem posicao de stop do programa = 8
		new Word(Opcode.JMPIE, 7, 0, 0), // 4 se r0=0 pula para r7(=8)
		new Word(Opcode.MULT, 1, 0, -1), // 5 r1 = r1 * r0
		new Word(Opcode.SUB, 0, 6, -1), // 6 decrementa r0 1
		new Word(Opcode.JMP, -1, -1, 4), // 7 vai p posicao 4
		new Word(Opcode.STD, 1, -1, 10), // 8 coloca valor de r1 na posicao 10
		new Word(Opcode.STOP, -1, -1, -1), // 9 stop
		new Word(Opcode.DATA, -1, -1, -1) }; // 10 ao final o valor do fatorial estara na posição 10 da memória


	static Word[] minimalProgram = new Word[] {
		new Word(Opcode.LDI, 0, -1, 999),
		new Word(Opcode.STD, 0, -1, 7),
		new Word(Opcode.STD, 0, -1, 8),
		new Word(Opcode.STD, 0, -1, 9),
		new Word(Opcode.STD, 0, -1, 10),
		new Word(Opcode.STD, 0, -1, 11),
		new Word(Opcode.STOP, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1) };

	static Word[] fibonacci = new Word[] { // mesmo que prog exemplo, so que usa r0 no lugar de r8
		new Word(Opcode.LDI, 1, -1, 0),
		new Word(Opcode.STD, 1, -1, 20),
		new Word(Opcode.LDI, 2, -1, 1),
		new Word(Opcode.STD, 2, -1, 21),
		new Word(Opcode.LDI, 0, -1, 22),
		new Word(Opcode.LDI, 6, -1, 6),
		new Word(Opcode.LDI, 7, -1, 31),
		new Word(Opcode.LDI, 3, -1, 0),
		new Word(Opcode.ADD, 3, 1, -1),
		new Word(Opcode.LDI, 1, -1, 0),
		new Word(Opcode.ADD, 1, 2, -1),
		new Word(Opcode.ADD, 2, 3, -1),
		new Word(Opcode.STX, 0, 2, -1),
		new Word(Opcode.ADDI, 0, -1, 1),
		new Word(Opcode.SUB, 7, 0, -1),
		new Word(Opcode.JMPIG, 6, 7, -1),
		new Word(Opcode.STOP, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1), // POS 20
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1) }; // ate aqui - serie de fibonacci ficara armazenada

	static Word[] factorialTrap = new Word[] {
		new Word(Opcode.LDI, 0, -1, 7), // numero para colocar na memoria
		new Word(Opcode.STD, 0, -1, 19),
		new Word(Opcode.LDD, 0, -1, 19),
		new Word(Opcode.LDI, 1, -1, -1),
		new Word(Opcode.LDI, 2, -1, 13), // SALVAR POS STOP
		new Word(Opcode.JMPIL, 2, 0, -1), // caso negativo pula pro STD
		new Word(Opcode.LDI, 1, -1, 1),
		new Word(Opcode.LDI, 6, -1, 1),
		new Word(Opcode.LDI, 7, -1, 13),
		new Word(Opcode.JMPIE, 7, 0, 0), // POS 9 pula pra STD (Stop-1)
		new Word(Opcode.MULT, 1, 0, -1),
		new Word(Opcode.SUB, 0, 6, -1),
		new Word(Opcode.JMP, -1, -1, 9), // pula para o JMPIE
		new Word(Opcode.STD, 1, -1, 18),
		new Word(Opcode.LDI, 8, -1, 2), // escrita
		new Word(Opcode.LDI, 9, -1, 18), // endereco com valor a escrever
		new Word(Opcode.TRAP, -1, -1, -1),
		new Word(Opcode.STOP, -1, -1, -1), // POS 17
		new Word(Opcode.DATA, -1, -1, -1), // POS 18
		new Word(Opcode.DATA, -1, -1, -1), };

	static Word[] fibonacciTrap = new Word[] { // mesmo que prog exemplo, so que usa r0 no lugar de r8
		new Word(Opcode.LDI, 8, -1, 1), // leitura
		new Word(Opcode.LDI, 9, -1, 56), // endereco a guardar
		new Word(Opcode.TRAP, -1, -1, -1),
		new Word(Opcode.LDD, 7, -1, 56), // numero do tamanho do fib
		new Word(Opcode.LDI, 3, -1, 0),
		new Word(Opcode.ADD, 3, 7, -1),
		new Word(Opcode.LDI, 4, -1, 36), // posicao para qual ira pular (stop) *
		new Word(Opcode.LDI, 1, -1, -1), // caso negativo
		new Word(Opcode.STD, 1, -1, 41),
		new Word(Opcode.JMPIL, 4, 7, -1), // pula pra stop caso negativo *
		new Word(Opcode.JMPIE, 4, 7, -1), // pula pra stop caso 0
		new Word(Opcode.ADDI, 7, -1, 41), // fibonacci + posicao do stop
		new Word(Opcode.LDI, 1, -1, 1),
		new Word(Opcode.STD, 1, -1, 41), // 25 posicao de memoria onde inicia a serie de fibonacci gerada
		new Word(Opcode.SUBI, 3, -1, 1), // se 1 pula pro stop
		new Word(Opcode.JMPIE, 4, 3, -1),
		new Word(Opcode.ADDI, 3, -1, 1),
		new Word(Opcode.LDI, 2, -1, 1),
		new Word(Opcode.STD, 2, -1, 42),
		new Word(Opcode.SUBI, 3, -1, 2), // se 2 pula pro stop
		new Word(Opcode.JMPIE, 4, 3, -1),
		new Word(Opcode.LDI, 0, -1, 43),
		new Word(Opcode.LDI, 6, -1, 25), // salva posicao de retorno do loop
		new Word(Opcode.LDI, 5, -1, 0), // salva tamanho
		new Word(Opcode.ADD, 5, 7, -1),
		new Word(Opcode.LDI, 7, -1, 0), // zera (inicio do loop)
		new Word(Opcode.ADD, 7, 5, -1), // recarrega tamanho
		new Word(Opcode.LDI, 3, -1, 0),
		new Word(Opcode.ADD, 3, 1, -1),
		new Word(Opcode.LDI, 1, -1, 0),
		new Word(Opcode.ADD, 1, 2, -1),
		new Word(Opcode.ADD, 2, 3, -1),
		new Word(Opcode.STX, 0, 2, -1),
		new Word(Opcode.ADDI, 0, -1, 1),
		new Word(Opcode.SUB, 7, 0, -1),
		new Word(Opcode.JMPIG, 6, 7, -1), // volta para o inicio do loop
		new Word(Opcode.STOP, -1, -1, -1), // POS 36
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1), // POS 41
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1) }; // POS 56

	static Word[] pb = new Word[] {
		// dado um inteiro em alguma posicao de memoria,
		// se for negativo armazena -1 na saida; se for positivo responde o fatorial do
		// numero na saida
		new Word(Opcode.LDI, 0, -1, 7), // numero para colocar na memoria
		new Word(Opcode.STD, 0, -1, 16),
		new Word(Opcode.LDD, 0, -1, 16),
		new Word(Opcode.LDI, 1, -1, -1),
		new Word(Opcode.LDI, 2, -1, 13), // SALVAR POS STOP
		new Word(Opcode.JMPIL, 2, 0, -1), // caso negativo pula pro STD
		new Word(Opcode.LDI, 1, -1, 1),
		new Word(Opcode.LDI, 6, -1, 1),
		new Word(Opcode.LDI, 7, -1, 13),
		new Word(Opcode.JMPIE, 7, 0, 0), // POS 9 pula pra STD (Stop-1)
		new Word(Opcode.MULT, 1, 0, -1),
		new Word(Opcode.SUB, 0, 6, -1),
		new Word(Opcode.JMP, -1, -1, 9), // pula para o JMPIE
		new Word(Opcode.STD, 1, -1, 15),
		new Word(Opcode.STOP, -1, -1, -1), // POS 14
		new Word(Opcode.DATA, -1, -1, -1), // POS 15
		new Word(Opcode.DATA, -1, -1, -1) };

	/**
	 * Assign a name for every program.
	 */
	private static final Map<String, Word[]> programs = Map.ofEntries(
		Map.entry("fac", factorial),
		Map.entry("factrap", factorialTrap),
		Map.entry("min", minimalProgram),
		Map.entry("fib", fibonacci),
		Map.entry("fibtrap", fibonacciTrap),
		Map.entry("pb", pb)
	);
}
