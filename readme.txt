Bernardo Barzoto Zomer, Felipe Elsner da Silva, Lucas Marchesan Cunha

Seção implementação:

1.1 CPU:
- Nossa CPU é dividida em duas classes, o CPU e o CPUState. A CPU armazena as instruções e é responsável por executá-las (podendo ou não imprimir essa execução com base se o trace esta ativado ou não), assim como garantir que seu funcionamento ocorra sem problemas (checando por interrupções de erro durante a execução e pela interrupção que sinaliza o fim do programa). Já o CPUState guarda os seus registradores, incluindo o pc, o IR e os 10 registradores que ela usa.

1.2 Memória:
- A Memória está implementada na classe Memory, armazenando tanto os tamanhos da memória quanto dos frames, além da memória em si, uma matriz que possui como medidas o tamanho da memória/tamanho dos frames e o tamanho dos frames. Assim, já temos a memória salva como suas partições de frames, que serão usados para armazenar os processos usando o método de paginação.

2.0 Programas:
- Os programas estão armazenados na classe Programs, que possui tanto os programas em si quanto um Map, que liga eles a uma palavra que sera usada para chamar o programa “nome do programa”.

3.0 Interrupções:
- Interrupções são definidas na classe Interrupts e tratadas na classe InterruptHandler, que é responsável por lidar com essas mensagens conforme elas aparecem. As checagem de interrupções em si estão espalhadas pelo código onde elas podem ocorrer, principalmente nas instruções da CPU, onde cada instrução é testada para ver se alguma das interrupções que ela pode causar é ativada.

4.0 Chamadas de sistema:
- A chamada de sistema foi implementada na classe SyscallHandler, que implementa a função TRAP. Nela, o registrador 8 define a natureza da operação, onde quando ele for 1 o sistema deve realizar uma leitura e armazenar esse valor no registrador apontado pelo registrador 9. Caso o registrador 8 for 2, ele realiza uma impressão do valor armazenado no registrador apontado pelo registrador 9.

5.0 Gerente de Memoria:
- O gerente de memória foi implementado na classe MemoryManager, onde é armazenada uma copia da memória, o número de frames ocupados é um vetor do tamanho da memória que, para cada frame da memória, diz se ele esta ocupado ou não. A partir desses elementos, o programa sabe quantos e quais frames a memória tem disponível e, a partir disso, consegue alocar e desalocar propriamente para cada processo que entra ou sai dela, caso ela tenha espaço suficiente disponível no caso de uma inserção. A conversão do endereço fisico pro lógico é  realizado na CPU, no método translateToPhysical e a tabela de páginas é armazenada no PCB de cada processo, no vetor frames que representa os frames ocupados por cada processo.

6.0 Gerente de Processos:
- O gerenciamento de processos é realizado por duas classes, o ProcessManager e o PCB. O PCB armazena os dados que referem ao código e sua execução, incluindo o seu id, seu pc, seu estado e a sua tabela de paginas. Já o ProcessManager é responsável pela criação e eliminação de processos, fazendo uso de MemoryManager para liberar os frames que um processo ocupa ou alocar frames para um processo ocupar dentre os disponíveis na memória.


Seção de testes:

Para executar o código, compile todos os *.java e execute "java OperatingSystem".

Programas:
- ”fac” para o código fatorial
- ”factrap” para o código fatorialTRAP
- “min” para o código progMinimo
- ”fib” para o código fibonacci10
- ”fibtrap” para o código fibonacciTRAP
- “pb” para o código PB
- “pc” para o código PC

Comandos:
- “new <nomeDePrograma>” equivale ao comando “cria <nomeDePrograma>”
- “ps” equivale ao comando “listaProcessos”
- “pdump <id>” equivale ao comando “dump <id>”
- “kill <id>” equivale ao comando “desloca <id>”
- “mdump <inicio,fim>” equivale ao comando “dumpM <inicio,fim>”
- “run <id>” equivale ao comando “executa <id>”
- “trace” equivale aos comandos “traceOn e traceOff”, invertendo o valor atual da variável trace
- “exit” equivale ao comando “exit”

Notas:
- O programa "pc" não executa corretamente
- O código não testa se um endereço de memória é válido antes de fazer o endereçamento; ele sempre assume que o endereço é valido
- O código nem sempre funciona quando há ou houve mais de um processo em memória
- Os programas fornecidos foram alterados pois continham bugs, instruções que tentam acessar memória não alocada
